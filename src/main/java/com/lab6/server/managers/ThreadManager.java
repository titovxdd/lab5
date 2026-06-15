package com.lab6.server.managers;

import com.lab6.common.Sup.*;
import com.lab6.common.validators.ArgumentValidator;
import com.lab6.server.Commands.AskingCommand;
import com.lab6.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadManager {
    private static volatile ThreadManager instance;

    private final ExecutorService readPool;
    private final ForkJoinPool processPool;
    private final ExecutorService writePool;

    private Executor executor;
    private CommandManager commandManager;
    private ServerSocket serverSocket;
    private volatile boolean running;

    private ThreadManager() {
        this.readPool = Executors.newCachedThreadPool();
        this.processPool = ForkJoinPool.commonPool();
        this.writePool = Executors.newCachedThreadPool();
        this.running = true;
    }

    public static ThreadManager getInstance() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    instance = new ThreadManager();
                }
            }
        }
        return instance;
    }

    public void startServer(CommandManager commandManager, int port) throws IOException {
        this.commandManager = commandManager;
        this.executor = new Executor(commandManager);
        this.serverSocket = new ServerSocket(port);
        Server.logger.info("Server started on port " + port);

        Thread acceptorThread = new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Server.logger.info("Client connected: " + clientSocket.getRemoteSocketAddress());

                    
                    sendCommandsList(clientSocket);

                    
                    handleClient(clientSocket);

                } catch (SocketException e) {
                    if (running) Server.logger.severe("Socket error: " + e.getMessage());
                } catch (IOException e) {
                    Server.logger.severe("Accept error: " + e.getMessage());
                }
            }
        });
        acceptorThread.setDaemon(true);
        acceptorThread.start();
        Server.logger.info("Server ready");
    }

    private void sendCommandsList(Socket clientSocket) throws IOException {
        try {
            Map<String, Pair<ArgumentValidator, Boolean>> commandsData = new HashMap<>();
            commandManager.getCommandsMap().forEach((name, cmd) -> {
                commandsData.put(name, new Pair<>(
                        cmd.getArgumentValidator(),
                        AskingCommand.class.isAssignableFrom(cmd.getClass())
                ));
            });

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(new Response(commandsData));
            }
            byte[] data = baos.toByteArray();

            
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            dos.writeInt(data.length);
            dos.write(data);
            dos.flush();

            Server.logger.info("Commands list sent (sync)");
        } catch (IOException e) {
            Server.logger.severe("Failed to send commands list: " + e.getMessage());
            throw e; 
        }
    }

    private void handleClient(Socket clientSocket) {
        readPool.submit(() -> {
            while (running && !clientSocket.isClosed()) {
                try {
                    
                    Request request = receiveRequest(clientSocket);
                    Server.logger.info("Request received: " + request);

                    final Request finalRequest = request;

                    processPool.submit(() -> {
                        Response response = processRequest(finalRequest);

                        writePool.submit(() -> {
                            sendResponse(clientSocket, response);
                        });
                    });

                } catch (EOFException e) {
                    Server.logger.info("Client disconnected: " + clientSocket.getRemoteSocketAddress());
                    closeSocket(clientSocket);
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    Server.logger.severe("Error reading request: " + e.getMessage());
                    closeSocket(clientSocket);
                    break;
                }
            }
        });
    }

    
    private Request receiveRequest(Socket clientSocket) throws IOException, ClassNotFoundException {
        
        ObjectInputStream ois = getObjectInputStream(clientSocket);
        Request request = (Request) ois.readObject();

        if (request.getUser() != null && request.getUser().getSecond() != null) {
            String hashedPassword = PasswordHasher.hash(request.getUser().getSecond());
            request.getUser().setSecond(hashedPassword);
        }
        return request;
    }


    
    private final ConcurrentHashMap<Socket, ObjectInputStream> inputStreams = new ConcurrentHashMap<>();

    private ObjectInputStream getObjectInputStream(Socket socket) throws IOException {
        return inputStreams.computeIfAbsent(socket, s -> {
            try {
                return new ObjectInputStream(s.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Response processRequest(Request request) {
        AuthenticatedExecutor authExecutor = new AuthenticatedExecutor(executor);
        ExecutionStatus status = authExecutor.runCommand(
                request.getCommand(),
                request.getBand(),
                request.getUser()
        );

        if (!status.isSuccess()) {
            Server.logger.warning("Command failed: " + status.getMessage());
        }

        return new Response(status);
    }

    
    private void sendResponse(Socket clientSocket, Response response) {
        try {
            Server.logger.info("Sending response...");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(response);
            }
            byte[] data = baos.toByteArray();
            Server.logger.info("Response size: " + data.length + " bytes");


            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            dos.writeInt(data.length);
            dos.write(data);
            dos.flush();

            Server.logger.info("Response sent");
        } catch (IOException e) {
            Server.logger.severe("Error sending response: " + e.getMessage());
            closeSocket(clientSocket);
        }
    }

    private void closeSocket(Socket socket) {
        try {
            
            ObjectInputStream ois = inputStreams.remove(socket);
            if (ois != null) ois.close();

            if (socket != null && !socket.isClosed()) {
                socket.close();
                Server.logger.info("Connection closed: " + socket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            Server.logger.severe("Error closing socket: " + e.getMessage());
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            Server.logger.severe("Error closing server: " + e.getMessage());
        }
        readPool.shutdown();
        processPool.shutdown();
        writePool.shutdown();
        Server.logger.info("Server stopped");
    }
}