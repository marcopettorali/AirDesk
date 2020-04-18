package airdesk;

import java.io.*;
import java.net.*;
import java.util.*;

public class Connections {

    public static String broadcastAddress;
    public static String subnetAddress;
    public static InetAddress localHost;

    static {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            localHost = socket.getLocalAddress();

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            int prefixLength = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
            byte[] localhostAddress = localHost.getAddress();

            int[] subnetMask = new int[4];
            int[] subnetMaskNegated = new int[4];

            int counter = prefixLength;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 8; j++) {
                    if (counter > 0) {
                        subnetMask[i] = Integer.rotateLeft(subnetMask[i], 1);
                        subnetMask[i] |= 1;
                        counter--;
                    } else {
                        subnetMaskNegated[i] = Integer.rotateLeft(subnetMaskNegated[i], 1);
                        subnetMaskNegated[i] |= 1;
                    }
                }
            }

            subnetAddress = "";
            broadcastAddress = "";
            int[] subnetAddressInt = new int[4];
            int[] broadcastAddressInt = new int[4];
            for (int i = 0; i < 4; i++) {
                Byte localhostByte = localhostAddress[i];
                int localhostInt = localhostByte.intValue();

                subnetAddressInt[i] = localhostInt & subnetMask[i];
                broadcastAddressInt[i] = subnetAddressInt[i] | subnetMaskNegated[i];

                subnetAddress += subnetAddressInt[i];
                broadcastAddress += broadcastAddressInt[i];

                if (i != 3) {
                    subnetAddress += ".";
                    broadcastAddress += ".";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendHelloMessageBroadcast() {
        String msg = "HIIM_M" + AirDesk.username;
        byte[] buffer = msg.getBytes();
        InetAddress address;
        try {

            address = InetAddress.getByName(broadcastAddress);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 7777);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            datagramSocket.send(packet);
            System.out.println("Sent '" + msg + "' msg in broadcast.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendHelloMessageResponseToAddress(InetAddress addr) {
        String msg = "HIIM_R" + AirDesk.username;
        byte[] buffer = msg.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 7777);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.send(packet);
            System.out.println("Sent '" + msg + "' msg to " + addr.getHostAddress() + ".");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendByeMessageBroadcast() {
        String msg = "BYE_" + AirDesk.username;
        byte[] buffer = msg.getBytes();
        InetAddress address;
        try {
            address = InetAddress.getByName(broadcastAddress);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 7777);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            datagramSocket.send(packet);
            System.out.println("Sent '" + msg + "' msg in broadcast.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendListRequestToAddress(InetAddress addr) {
        String msg = "LIST";
        byte[] buffer = msg.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 7777);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.send(packet);
            System.out.println("Sent '" + msg + "' msg to " + addr.getHostAddress() + ".");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendFilesListToAddress(InetAddress addr) {

        try {
            List<FileBean> files = AirDesk.retrieveFileList();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(files);
            outputStream.close();

            byte[] listData = out.toByteArray();

            String msg = "FILE";
            byte[] msgBytes = msg.getBytes();

            byte[] buffer = new byte[msgBytes.length + listData.length];

            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = i < msgBytes.length ? msgBytes[i] : listData[i - msgBytes.length];
            }

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 7777);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.send(packet);
            System.out.println("Sent '" + msg + "' msg to " + addr.getHostAddress() + ".");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
