package airdesk;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

public class AirDesk extends Application {
    
    public static String username = "PC Fisso";  
    private AirDeskGUI gui = new AirDeskGUI();
    
    @Override
    public void start(Stage primaryStage) {
        
        UDPListener server = new UDPListener(7799);
        server.setDaemon(true);
        server.start();
        
        Connections.sendHelloMessageBroadcast();
       
        Scene scene = new Scene(gui);
        primaryStage.setTitle("AirDesk");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
