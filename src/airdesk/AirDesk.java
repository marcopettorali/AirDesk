package airdesk;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

public class AirDesk extends Application {

    public static String username = "PC Portatile";
    private AirDeskGUI gui = new AirDeskGUI();

    public static List<FileBean> retrieveFileList() {
        final File folder = new File("./shared/");
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

    @Override
    public void start(Stage primaryStage) {

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
