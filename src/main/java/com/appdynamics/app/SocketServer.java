package com.appdynamics.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mohitagarwal on 09/08/17.
 */
public class SocketServer extends Thread {
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public SocketServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(0);
    }

    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for client on port: " + serverSocket.getLocalPort());
                Socket server = serverSocket.accept();
                SocketConnectProcessor socketConnectProcessor = new SocketConnectProcessor(server);
                executorService.submit(socketConnectProcessor);
            } catch(SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException io) {
                io.printStackTrace();
                break;
            }
        }
    }
}
