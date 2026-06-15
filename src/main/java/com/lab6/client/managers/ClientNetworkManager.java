package com.lab6.client.managers;

import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class ClientNetworkManager {
    private final int PORT;
    private final String SERVER_HOST;
    private SocketChannel channel;

    
    private ObjectOutputStream objectOut;

    public ClientNetworkManager(int port, String host) {
        this.PORT = port;
        this.SERVER_HOST = host;
    }

    
    public void connect() throws IOException {
        channel = SocketChannel.open();
        
        channel.connect(new InetSocketAddress(SERVER_HOST, PORT));

        
        
        objectOut = new ObjectOutputStream(Channels.newOutputStream(channel));
    }

    
    public void send(Request request) throws IOException {
        objectOut.writeObject(request);
        
        objectOut.reset();
        objectOut.flush();
    }

    
    public Response receive() throws IOException, ClassNotFoundException {
        
        channel.socket().setSoTimeout(5000);

        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        while (lengthBuffer.hasRemaining()) {
            if (channel.read(lengthBuffer) == -1) {
                throw new EOFException("Сервер закрыл соединение");
            }
        }
        lengthBuffer.flip();
        int length = lengthBuffer.getInt();

        
        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
        while (dataBuffer.hasRemaining()) {
            if (channel.read(dataBuffer) == -1) {
                throw new EOFException("Сервер закрыл соединение при чтении данных");
            }
        }
        dataBuffer.flip();

        
        byte[] data = new byte[dataBuffer.remaining()];
        dataBuffer.get(data);

        
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (Response) input.readObject();
        }
    }

    
    public void close() throws IOException {
        if (objectOut != null) {
            objectOut.close();
        }
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }

    public boolean isConnected() {
        return channel != null && channel.isOpen() && channel.socket().isConnected();
    }
}