package com.appdynamics.app;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by mohitagarwal on 14/08/17.
 */
public class SocketConnectProcessor implements Runnable {
    private Socket client;
    private static String reqFwdClientType = System.getProperty("client", "socket");
    int DEFAULT_TIMEOUT = 1500;

    SocketConnectProcessor(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Just connected to " + client.getRemoteSocketAddress());
        try {
            DataInputStream in = new DataInputStream(client.getInputStream());
            String clientMsg = in.readUTF();

            if (checkAndForwardRequest(clientMsg)) {
                System.out.println("Request forwarded");
            } else {
                System.out.println("No request forwarded");
            }

            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF("Thank you for connecting to " + client.getLocalSocketAddress()
                    + "\nGoodbye!");
            client.close();
        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private boolean checkAndForwardRequest(String clientMsg) {
        boolean isRequestFwd = false;
        Map<String, String> processedMsg = Utils.processSocketMessage(clientMsg);
        System.out.println("Processed Msg: " + processedMsg.toString());
        String ipFwdChainHeader = processedMsg.get("IP-Fwd-Chain");

        if (ipFwdChainHeader != null && !ipFwdChainHeader.isEmpty()) {
            isRequestFwd = fwdIpReq(ipFwdChainHeader, processedMsg);
        }
        return isRequestFwd;
    }

    private boolean fwdIpReq(String ipFwdChainHeader, Map<String, String> processedMsg) {
        boolean isRequestFwd = false;

        System.out.println("Checking for forwarding in chain " + ipFwdChainHeader);
        String fwdIPAddr = ipFwdChainHeader;
        String newFwdChainHeader = ipFwdChainHeader;
        final int EoIP = ipFwdChainHeader.indexOf(";");
        if (EoIP > 1) {
            System.out.println();
            fwdIPAddr = ipFwdChainHeader.substring(0, EoIP);
            newFwdChainHeader = ipFwdChainHeader.substring(EoIP + 1);
            System.out.println("New fowd Chain: " + newFwdChainHeader);
            try {
                Thread.sleep(Long.valueOf(DEFAULT_TIMEOUT));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            processedMsg.put("IP-Fwd-Chain", newFwdChainHeader);
            Utils.fwdSocketRequest(fwdIPAddr, processedMsg);
            isRequestFwd = true;
        }
        return isRequestFwd;
    }
}
