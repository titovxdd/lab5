package com.lab6.client.managers;

import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientNetworkManager {
    private final int PORT;
    private final String SERVER_HOST;
    private SocketChannel channel;

    public ClientNetworkManager(int port, String host) {
        this.PORT = port;
        this.SERVER_HOST = host;
    }

    public void connect() throws IOException {
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(SERVER_HOST, PORT));
    }

    public void close() throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }

    public void send(Request request) throws IOException {
        ByteArrayOutputStream breq = new ByteArrayOutputStream();
        try (ObjectOutputStream req = new ObjectOutputStream(breq)) {
            req.writeObject(request);
        }

        byte[] data = breq.toByteArray();

        ByteBuffer dataBuffer = ByteBuffer.wrap(data);
        while (dataBuffer.hasRemaining()) {
            channel.write(dataBuffer);
        }
    }

    public Response receive() throws IOException, ClassNotFoundException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        while (lengthBuffer.hasRemaining()) {
            int read = channel.read(lengthBuffer);
            if (read == -1) {
                throw new IOException("Server closed connection");
            }
        }
        lengthBuffer.flip();
        int length = lengthBuffer.getInt();

        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
        while (dataBuffer.hasRemaining()) {
            int read = channel.read(dataBuffer);
            if (read == -1) {
                throw new IOException("Server closed connection while reading data");
            }
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(dataBuffer.array()))) {
            return (Response) input.readObject();
        }
    }

    public boolean isConnected() {
        return channel != null && channel.isConnected() && channel.isOpen();
    }
}
