package airdesk;

import java.io.*;
import java.net.*;

public class TCPListener extends Thread {

    private DatagramSocket serverSocket;

    public TCPListener() {
        super();
    }

    private void receiveWantMessage(Socket socket) {
        System.out.println("Received want msg");
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String filename = dis.readUTF();
            FileInputStream fis = new FileInputStream(filename);

            while (true) {
                byte[] bytes = new byte[1024];
                int bytesRead = fis.read(bytes);
                dos.write(bytes);
                dos.writeInt(bytesRead);

                if (bytesRead != bytes.length) {
                    fis.close();
                    dis.close();
                    dos.close();
                    socket.close();
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void receiveGiveMessage(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String filename = dis.readUTF();

            TCPConnection.sendWantMessageToAddress(socket.getInetAddress(), filename);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(7778)) {
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(connectionSocket.getInputStream());

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
        }
    }
}
