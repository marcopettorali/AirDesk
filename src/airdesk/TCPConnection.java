package airdesk;

import java.io.*;
import java.net.*;

public class TCPConnection {

    public static void sendWantMessageToAddress(InetAddress addr, String path) {
        try (
                Socket sock = new Socket(addr, 7778);
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                DataInputStream dis = new DataInputStream(sock.getInputStream());) {

            dos.writeUTF("WANT");
            dos.writeUTF(path);

            FileOutputStream fos = new FileOutputStream("./shared" + new File(path).getName(), true);

            while (true) {
                byte[] buffer = new byte[1024];
                dis.read(buffer);
                int bytesRead = dis.readInt();
                fos.write(buffer, 0, bytesRead);
                if (bytesRead != 1024) {
                    System.out.println("TRANSFER COMPLETED");
                    dis.close();
                    dos.close();
                    fos.close();
                    sock.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendGiveMessageToAddress(InetAddress addr, String path) {
        try (
                Socket sock = new Socket(addr, 7778);
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                DataInputStream dis = new DataInputStream(sock.getInputStream());) {

            dos.writeUTF("GIVE");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
