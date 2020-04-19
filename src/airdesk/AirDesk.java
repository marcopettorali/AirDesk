package airdesk;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

public class AirDesk extends Application {

    public static String username = "PC Fisso";
    public static String sharedFolder = "./shared/";
    private AirDeskGUI gui = new AirDeskGUI();

    public static List<FileBean> retrieveFileList() {
        final File folder = new File(sharedFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        List<FileBean> result = new ArrayList<>();

        for (final File f : folder.listFiles()) {
            if (f.isFile()) {
                result.add(new FileBean(f.getAbsolutePath(), f.getName(), new DecimalFormat("###,###").format(f.length() / 1024)));
            }
        }
        return result;
    }

    private static void loadParametersFromConfigFile() {
        try {
            Scanner scanner = new Scanner(new File("./config.txt"));        
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] words = line.split(":");
                switch (words[0].trim()) {
                    case "username":
                        username = words[1].trim();
                        break;
                    case "shared-folder":
                        sharedFolder = words[1].trim();
                        break;
                }
                System.out.println(line);
            }
            System.out.println(username + sharedFolder);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {

        loadParametersFromConfigFile();
        UDPListener server = new UDPListener(7777);
        server.setDaemon(true);
        server.start();

        TCPListener tcpListener = new TCPListener();
        tcpListener.setDaemon(true);
        tcpListener.start();

        Scene scene = new Scene(gui);
        primaryStage.setTitle("AirDesk");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        UDPConnection.sendHelloMessageBroadcast();

    }

    @Override
    public void stop() {
        UDPConnection.sendByeMessageBroadcast();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
