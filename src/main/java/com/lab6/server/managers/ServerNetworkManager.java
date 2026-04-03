package com.lab6.server.managers;

import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNetworkManager {
    private final int PORT;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public ServerNetworkManager(int port) {
        this.PORT = port;
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        Server.logger.info("Server started on port: " + PORT);
    }

    public Socket acceptConnection() throws IOException {
        clientSocket = serverSocket.accept();
        Server.logger.info("Client connected: " + clientSocket.getInetAddress());
        return clientSocket;
    }

    public Request receive(Socket clientSocket) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            Server.logger.info("Request received from client");
            return (Request) in.readObject();
        }
    }

    public void send(Response response, Socket clientSocket) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            out.writeObject(response);
            out.flush();
            Server.logger.info("Response sent to client: " + clientSocket.getInetAddress());
        }
    }

    public void closeConnection(Socket clientSocket) {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            Server.logger.warning("Error closing client connection: " + e.getMessage());
        }
    }

    public void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        Server.logger.info("Server stopped");
    }

    public void close() throws IOException {
        stopServer();
    }
}
