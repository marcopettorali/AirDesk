package airdesk;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javafx.application.Platform;

public class TCPConnection {

    private static int numberOfPackets;
    private static int packetsCounter;

    private static void incrementCounter() {
        AirDeskGUI.advanceLogProgress((int) (packetsCounter++ * 100 / numberOfPackets));
    }

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
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String timestamp = formatter.format(date);
                fileName = timestamp + " " + fileName;
            }

            fos = new FileOutputStream(AirDesk.sharedFolder + fileName, true);
            byte[] buffer = new byte[1024];
            numberOfPackets = dis.readInt();
            packetsCounter = 1;
            AirDeskGUI.setupLogProgress();
            while (true) {
                int bytesRead = dis.read(buffer);
                dos.writeUTF("ACK");
                fos.write(buffer, 0, bytesRead);
                Platform.runLater(() -> {
                    incrementCounter();
                });
                if (bytesRead != 1024) {

                    AirDeskGUI.addLogEntry("File " + fileName + " received correctly.\n");
                    fos.close();
                    break;
                }
            }
            dos.flush();
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
            dos.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
