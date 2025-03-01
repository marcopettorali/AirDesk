package airdesk;

import java.io.*;
import java.net.*;
import javafx.application.Platform;

public class TCPListener extends Thread {

    private static int numberOfPackets;
    private static int packetsCounter;

    private static void incrementCounter() {
        AirDeskGUI.advanceLogProgress((int) (packetsCounter++ * 100 / numberOfPackets));
    }

    public TCPListener() {
        super();
    }

    private void receiveWantMessage(Socket socket) {
        System.out.println("Received want msg");
        DataOutputStream dos = null;
        DataInputStream dis = null;
        FileInputStream fis = null;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            String filename = dis.readUTF();
            fis = new FileInputStream(filename);
            byte[] bytes = new byte[1024];
            numberOfPackets = (int) Math.ceil(new File(filename).length() / 1024);
            dos.writeInt(numberOfPackets);
            packetsCounter = 1;
            AirDeskGUI.addLogEntry("Sending file: " + new File(filename).getName() + " to " + socket.getInetAddress().getHostAddress());
            while (true) {
                int bytesRead = fis.read(bytes);
                dos.write(bytes, 0, bytesRead);
                String ack = dis.readUTF();
                Platform.runLater(() -> {
                    incrementCounter();
                });

                if (!ack.equals("ACK")) {
                    System.out.println("ACK ERROR");
                    break;
                }
                if (bytesRead != bytes.length) {
                    break;
                }
            }
            AirDeskGUI.addLogEntry("Transfer completed");
            UDPConnection.sendListRequestToAddress(socket.getInetAddress());
            dos.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                dos.close();
                dis.close();
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void receiveGiveMessage(Socket socket) {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(socket.getInputStream());
            String filename = dis.readUTF();
            new Thread() {
                public void run() {
                    TCPConnection.sendWantMessageToAddress(socket.getInetAddress(), filename);
                }
            }.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                dis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void run() {
        DataInputStream dis = null;
        try (ServerSocket serverSocket = new ServerSocket(7778)) {
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                dis = new DataInputStream(connectionSocket.getInputStream());

                String command = dis.readUTF();
                System.out.println("Received command " + command + " from " + connectionSocket.getInetAddress().getHostAddress());
                switch (command) {
                    case "WANT":
                        receiveWantMessage(connectionSocket);
                        break;
                    case "GIVE":
                        receiveGiveMessage(connectionSocket);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
