package airdesk;

import java.io.*;
import java.net.*;
import javafx.application.*;

public class UDPListener extends Thread {

    private int port;

    public UDPListener(int port) {
        super();
        this.port = port;
    }

    private void receiveHelloMessage(InetAddress addr, String sentence) {

        String mode = sentence.substring(4, 6).trim();
        String name = sentence.substring(6).trim();
        Client client = new Client(name, addr);
        Platform.runLater(() -> {
            AirDeskGUI.clientsTableAddClient(client);
        });

        System.out.println("Received Hello msg from " + name + "@" + addr.getHostAddress());

        if (mode.equals("_M")) {
            Connections.sendHelloMessageResponseToAddress(addr);
        }
    }
    
    private void receiveByeMessage(InetAddress addr, String sentence) {
        String name = sentence.substring(4).trim();
        Platform.runLater(() -> {
            AirDeskGUI.clientsTableDeleteClient(name);
        });

        System.out.println("Received Bye msg from " + name + "@" + addr.getHostAddress());
    }

    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[50];

            System.out.printf("Listening on udp:%s:%d%n", Connections.localHost.getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {
                serverSocket.receive(receivePacket);
                if (receivePacket.getAddress().equals(Connections.localHost)) {
                    continue;
                }
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());              
                System.out.println("Received msg from " + receivePacket.getAddress() + ".");
                String command = sentence.substring(0, 4);
                switch (command) {
                    case "HIIM":
                        receiveHelloMessage(receivePacket.getAddress(), sentence);
                        break;
                    case "BYE_":
                        receiveByeMessage(receivePacket.getAddress(), sentence);
                        break;
                }

            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
