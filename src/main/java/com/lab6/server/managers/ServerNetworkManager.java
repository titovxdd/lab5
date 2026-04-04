package com.lab6.server.managers;

import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;
import com.lab6.server.Server;

import java.io.*;
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
        Server.logger.info("Server started");
    }

    public Socket acceptConnection() throws IOException {
        clientSocket = serverSocket.accept();
        Server.logger.info("Client connected");
        return clientSocket;
    }

    public Request receive(Socket clientSocket) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
        return (Request) input.readObject();
    }

    public void send(Response response, Socket clientSocket) throws IOException {
        ByteArrayOutputStream bres = new ByteArrayOutputStream();
        try (ObjectOutputStream res = new ObjectOutputStream(bres)) {
            res.writeObject(response);
        }
        byte[] data = bres.toByteArray();

        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        dos.writeInt(data.length);
        dos.write(data);
        dos.flush();
    }

    public void closeConnection(Socket clientSocket) {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            Server.logger.warning("Error closing client connection");
        }
    }

    public void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        Server.logger.info("Server stopped");
    }

}
