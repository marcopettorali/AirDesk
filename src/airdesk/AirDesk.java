package airdesk;


import java.net.InetAddress;
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

public class AirDesk extends Application {
    
    public static String username = "AirDesk1";  
    private AirDeskGUI gui = new AirDeskGUI();
    
    @Override
    public void start(Stage primaryStage) {
        
        UDPListener server = new UDPListener(7799);
        server.setDaemon(true);
        server.start();
        
        //Connections.sendHelloMessageBroadcast();
       
        Scene scene = new Scene(gui);
        primaryStage.setTitle("AirDesk");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        try {
            Client c1 = new Client("Prova1", InetAddress.getByName("192.168.1.1"));
            Client c2 = new Client("Prova2", InetAddress.getByName("192.168.1.2"));
            Client c3 = new Client("Prova3", InetAddress.getByName("192.168.1.3"));
            Client c4 = new Client("Prova4", InetAddress.getByName("192.168.1.4"));
            List<Client> clients = new ArrayList<>();
            clients.add(c1);
            clients.add(c2);
            clients.add(c3);
            clients.add(c4);
            
            gui.clientsTableSetElements(clients);
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
