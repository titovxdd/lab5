package com.lab6.server.managers;

import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;
import com.lab6.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerNetworkManager {
    private final int PORT;
    private ServerSocket serverSocket;
    private final ConcurrentHashMap<Socket, ObjectInputStream> inputStreams = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Socket, DataOutputStream> outputStreams = new ConcurrentHashMap<>();

    public ServerNetworkManager(int port) {
        this.PORT = port;
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        Server.logger.info("Server started on port: " + PORT);
    }

    public Socket acceptConnection() throws IOException {
        Socket clientSocket = serverSocket.accept();
        Server.logger.info("Client connected: " + clientSocket.getRemoteSocketAddress());

        // ✅ Создаём ObjectInputStream для ЧТЕНИЯ (без длины)
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        inputStreams.put(clientSocket, ois);

        // ✅ Создаём DataOutputStream для ОТПРАВКИ (с длиной)
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        outputStreams.put(clientSocket, dos);

        return clientSocket;
    }

    /**
     * Получение запроса БЕЗ длины (прямое чтение ObjectInputStream)
     */
    public Request receive(Socket clientSocket) throws IOException, ClassNotFoundException {
        ObjectInputStream input = inputStreams.get(clientSocket);
        if (input == null) {
            throw new IOException("No input stream for client");
        }
        return (Request) input.readObject();
    }

    /**
     * Отправка ответа С длиной (DataOutputStream + длина)
     */
    public void send(Response response, Socket clientSocket) throws IOException {
        DataOutputStream dos = outputStreams.get(clientSocket);
        if (dos == null) {
            throw new IOException("No output stream for client");
        }

        // Сериализуем ответ в байты
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
        }
        byte[] data = baos.toByteArray();

        // Отправляем длину, затем данные
        dos.writeInt(data.length);
        dos.write(data);
        dos.flush();

        Server.logger.info("Response sent to client (length: " + data.length + ")");
    }

    public void closeConnection(Socket clientSocket) {
        try {
            ObjectInputStream ois = inputStreams.remove(clientSocket);
            if (ois != null) ois.close();

            DataOutputStream dos = outputStreams.remove(clientSocket);
            if (dos != null) dos.close();

            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            Server.logger.info("Connection closed: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            Server.logger.warning("Error closing connection: " + e.getMessage());
        }
    }

    public void stopServer() throws IOException {
        for (Socket socket : inputStreams.keySet()) {
            closeConnection(socket);
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        Server.logger.info("Server stopped");
    }
}
