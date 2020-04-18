package airdesk;

import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

public class FilesTable extends TableView<FileBean> {

    private static ObservableList<FileBean> files;

    public FilesTable() {
        super();
        TableColumn fileNameColumn = new TableColumn("Name");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn filePathColumn = new TableColumn("Path");
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        
        TableColumn fileSizeColumn = new TableColumn("Size (KB)");
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));

        this.getColumns().addAll(fileNameColumn, filePathColumn, fileSizeColumn);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        files = FXCollections.observableArrayList(); 
    }

    public void setFiles(List<FileBean> files) {
        this.getItems().clear();
        this.files = FXCollections.observableArrayList();
        this.files.addAll(files);
        this.setItems(this.files);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
    }
    
    public void addFile(FileBean f){
        files.add(f);
        this.setItems(this.files);
    }
    
    public void deleteFile(String name){
        for(int i = 0; i< files.size(); i++){
            if(files.get(i).getName().equals(name)){
                files.remove(i);
                return;
            }
        }
    }
    
    public List<FileBean> getClients(){
        return files;
    }

    public FileBean getSelected() {
        return this.getSelectionModel().getSelectedItem();
    }

}
