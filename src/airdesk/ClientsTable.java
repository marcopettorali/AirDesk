package airdesk;

import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

public class ClientsTable extends TableView<Client> {

    private static ObservableList<Client> clients;

    public ClientsTable() {
        super();
        TableColumn clientNameColumn = new TableColumn("Name");
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn clientAddressColumn = new TableColumn("Address");
        clientAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        this.getColumns().addAll(clientNameColumn, clientAddressColumn);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        clients = FXCollections.observableArrayList(); 
    }

    public void setClients(List<Client> clients) {
        this.getItems().clear();
        this.clients = FXCollections.observableArrayList();
        this.clients.addAll(clients);
        this.setItems(this.clients);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for(Client c : clients){
            System.out.println(c.getName() + "@" + c.getAddress());
        }
    }
    
    public void deleteClient(String name){
        for(int i = 0; i< clients.size(); i++){
            if(clients.get(i).getName().equals(name)){
                clients.remove(i);
                return;
            }
        }
    }
    
    public List<Client> getClients(){
        return clients;
    }

    public Client getSelected() {
        return this.getSelectionModel().getSelectedItem();
    }

}
