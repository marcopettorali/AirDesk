package airdesk;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class TCPConnection {

    public static void sendWantMessageToAddress(InetAddress addr, String path) {
        FileOutputStream fos = null;
        try (
                Socket sock = new Socket(addr, 7778);
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                DataInputStream dis = new DataInputStream(sock.getInputStream());) {

            dos.writeUTF("WANT");
            dos.writeUTF(path);
            AirDeskGUI.addLogEntry("Remote file incoming: " + new File(path).getName());

            String fileName = new File(path).getName();
            if (new File(AirDesk.sharedFolder + fileName).exists()) {
                Date date = new Date();  
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
                String timestamp = formatter.format(date);        
                fileName = timestamp + "-" + fileName;
            }

            fos = new FileOutputStream(AirDesk.sharedFolder + fileName, true);

            while (true) {
                byte[] buffer = new byte[65000];
                dis.read(buffer);

                int bytesRead = dis.readInt();
                System.out.println(bytesRead);
                fos.write(buffer, 0, bytesRead);
                dos.writeUTF("ACK");
                if (bytesRead != 65000) {
                    AirDeskGUI.addLogEntry("File " + new File(path).getName() + " received correctly.");
                    fos.close();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void sendGiveMessageToAddress(InetAddress addr, String path) {
        try (
                Socket sock = new Socket(addr, 7778);
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                DataInputStream dis = new DataInputStream(sock.getInputStream());) {

            dos.writeUTF("GIVE");
            dos.writeUTF(path);
            AirDeskGUI.addLogEntry("Sending " + new File(path).getName() + " to " + AirDeskGUI.clientsTableGetNameFromAddress(addr));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
