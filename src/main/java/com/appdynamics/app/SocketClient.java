package com.appdynamics.app;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohitagarwal on 09/08/17.
 */
public class SocketClient extends Thread {
    private String serverName;
    private int serverPort;
    private int connCount;

    public SocketClient(String serverName, String port, String connCount) {
        this.serverName = serverName;
        this.serverPort = Integer.parseInt(port);
        this.connCount = Integer.parseInt(connCount);
    }

    public void run() {
        try {
            int i = 0;
            while (true) {
                System.out.println("Connecting to " + serverName + " on port " + serverPort);
                Socket client = new Socket(serverName, serverPort);

                System.out.println("Just connected to " + client.getRemoteSocketAddress());
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                Map<String, String> processedMsg = new HashMap<>();

                processedMsg.put("IP-Fwd-Chain", "127.0.0.1:5060;;");
                processedMsg.put("Payload", "payload");
                processedMsg.put("Connectio", "connection");
                processedMsg.put("Timeout", "time");

                String sockMsg = Utils.createSocketMessage(processedMsg);
                System.out.println("sockMsg: " + sockMsg);
                out.writeUTF("Hello from " + client.getLocalSocketAddress() + "\n"+sockMsg);
                InputStream inFromServer = client.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);

                System.out.println("Server says " + in.readUTF());
                client.close();
                if (connCount > 0) {
                    i++;
                    if (i == connCount ) {
                        break;
                    }
                }
                System.out.println("Client going to sleep for 3sec");
                Thread.sleep(3000);
                System.out.println("Client waking up");
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
