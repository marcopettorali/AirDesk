package airdesk;

import java.io.*;
import java.net.*;

public class TCPListener extends Thread {

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

            while (true) {
                byte[] bytes = new byte[65000];
                int bytesRead = fis.read(bytes);
                dos.write(bytes);
                dos.writeInt(bytesRead);
                String ack = dis.readUTF();
                if (!ack.equals("ACK")) {
                    System.out.println("ACK ERROR");
                    break;
                }
                if (bytesRead != bytes.length) {
                    break;
                }
            }

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

            TCPConnection.sendWantMessageToAddress(socket.getInetAddress(), filename);
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
