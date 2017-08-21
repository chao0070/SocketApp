package com.appdynamics.app;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohitagarwal on 14/08/17.
 */
public final class Utils {
    static int PAYLOAD_CHUNK_SIZE = 16 * 1024;
    static int PAYLOAD_MULIPLIER = 1024;

    public static String createSocketMessage(Map<String, String> fwdMsg) {
        String fwdMessage = "";
        for (Map.Entry<String, String> entry : fwdMsg.entrySet()) {
            fwdMessage += (entry.getKey() + "=" + entry.getValue() + "\n");
        }
        return fwdMessage;
    }

    public static void fwdSocketRequest(String fwdAddr, Map<String, String> fwdMsg) {
        String[] addrArray = fwdAddr.split(":");
        String ip = addrArray[0];
        int port = Integer.parseInt(addrArray[1]);
        try {
            Socket clientSocket = new Socket(ip, port);
            String fwdMsj = createSocketMessage(fwdMsg);
            OutputStream outToServer = clientSocket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("Hello from " + clientSocket.getLocalSocketAddress() + "\n"+fwdMsj);
            InputStream inFromServer = clientSocket.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());
            clientSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> processSocketMessage(String message) {
        Map<String, String> processedMessage = new HashMap<>();
        System.out.println("Message: " + message);
        String[] clientMsgSplit = message.split("\n");
        for(int i = 1; i < clientMsgSplit.length; i++) {
            String[] headerArr = clientMsgSplit[1].split("=");
            if (headerArr.length == 2) {
                processedMessage.put(headerArr[0], headerArr[1]);
            }
        }
        return processedMessage;
    }
}
