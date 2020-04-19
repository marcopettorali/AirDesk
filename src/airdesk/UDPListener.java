package airdesk;

import java.io.*;
import java.net.*;
import java.util.*;

import javafx.application.*;

public class UDPListener extends Thread {

    private int port;
    private DatagramSocket serverSocket;

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
            UDPConnection.sendHelloMessageResponseToAddress(addr);
        }
    }

    private void receiveByeMessage(InetAddress addr, String sentence) {
        String name = sentence.substring(4).trim();
        Platform.runLater(() -> {
            AirDeskGUI.clientsTableDeleteClient(name);
        });

        System.out.println("Received Bye msg from " + name + "@" + addr.getHostAddress());
    }

    private void receiveListMessage(InetAddress addr, String sentence) {
        System.out.println("Received List msg from " + addr.getHostAddress());
        UDPConnection.sendFilesListToAddress(addr);
    }

    private void receiveFileMessage(InetAddress addr, String sentence) {
        try {

            byte[] receiveData = new byte[50];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            String sizeStr;
            do {
                serverSocket.receive(receivePacket);
            } while (!receivePacket.getAddress().equals(addr));

            sizeStr = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (!sizeStr.substring(0, 4).equals("SIZE")) {
                System.out.println("ERROR IN RECEIVE FILE LIST SIZE");
            }
            int size = Integer.parseInt(sizeStr.substring(4).trim());

            byte[] data = new byte[size];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            do {
                serverSocket.receive(packet);
            } while (!packet.getAddress().equals(addr));

            ObjectInputStream inputStream = null;
            inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
            List<FileBean> files = (List<FileBean>) inputStream.readObject();
            Platform.runLater(() -> {
                AirDeskGUI.filesTableSetFiles(files);
            });

            System.out.println("Received File msg from " + addr.getHostAddress());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[50];

            System.out.printf("Listening on udp:%s", UDPConnection.localHost.getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {
                serverSocket.receive(receivePacket);
                if (receivePacket.getAddress().equals(UDPConnection.localHost)) {
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
                    case "LIST":
                        receiveListMessage(receivePacket.getAddress(), sentence);
                        break;
                    case "FILE":
                        receiveFileMessage(receivePacket.getAddress(), sentence);
                        break;
                }

            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
