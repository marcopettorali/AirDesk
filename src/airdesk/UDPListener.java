package airdesk;

import java.io.*;
import java.net.*;

public class UDPListener extends Thread {

    private int port;

    public UDPListener(int port) {
        super();
        this.port = port;
    }
    
    private void receiveHelloMessage(InetAddress addr, String sentence){
        String name = sentence.substring(4).trim();
        Client client = new Client(name, addr);
        AirDeskGUI.clientsTableAddClient(client);
        System.out.println("Received Hello msg from " + name + "@" + addr.getHostAddress());
    }

    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[50];

            System.out.printf("Listening on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {

                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received '" + sentence + "' msg from " + receivePacket.getAddress() + ".");
                String command = sentence.substring(0, 4);
                switch(command){
                    case "HIIM":
                        receiveHelloMessage(receivePacket.getAddress(), sentence);
                }

            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
