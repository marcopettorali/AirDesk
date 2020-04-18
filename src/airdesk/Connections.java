package airdesk;

import java.net.*;

public class Connections {

    public static void sendHelloMessageBroadcast() {
        String msg = "HIIM" + AirDesk.username;
        byte[] buffer = msg.getBytes();
        InetAddress address;
        try {
            address = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 7799);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            datagramSocket.send(packet);
            System.out.println("Sent '" + msg + "' msg in broadcast.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
