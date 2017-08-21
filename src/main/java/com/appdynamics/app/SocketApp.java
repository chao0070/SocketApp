package com.appdynamics.app;

/**
 * Created by mohitagarwal on 09/08/17.
 */
public class SocketApp {

    public static void main(String[] args) throws Exception {

        String appBehaviour = System.getProperty("app", "client");
        String initialSleep = System.getProperty("sleep", "-1");
        String serverName = System.getProperty("server.name", "localhost");
        String serverPort = System.getProperty("server.port", "5060");
        String clientConnCount = System.getProperty("client.conn.count", "-1");
        System.out.println("This app is: " + appBehaviour +
                "\nServer name is: " + serverName +
                "\nServer port is: " + serverPort);
        int initialS = Integer.parseInt(initialSleep);
        if (initialS < 0) {
            System.out.println("Going to sleep for 10ms"  + ".....");
            Thread.sleep(10);
            System.out.println("Waing up.....");
        } else {
            System.out.println("Going to sleep for " + initialSleep + ".....");
            Thread.sleep(Integer.parseInt(initialSleep) * 60000);
            System.out.println("Waing up.....");
        }

        try {
            Thread t;
            if (appBehaviour.equals("server")) {
                t = new SocketServer(Integer.parseInt(serverPort));
            } else {
                t = new SocketClient(serverName, serverPort, clientConnCount);

            }
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
