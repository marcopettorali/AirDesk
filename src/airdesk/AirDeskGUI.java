package airdesk;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class AirDeskGUI extends AnchorPane {

    private static HBox hBox;
    private static VBox vBox;
    private static Label label;
    private static ClientsTable clientsTable;
    private static Label logLabel;
    private static TextArea logTextArea;
    private static VBox vBox0;
    private static Label label0;
    private static FilesTable filesTable;
    private static Button downloadBtn;
    private static Label label1;
    private static StackPane dragAndDropPane;

    public AirDeskGUI() {

        hBox = new HBox();
        vBox = new VBox();
        label = new Label();
        clientsTable = new ClientsTable();
        logLabel = new Label();
        logTextArea = new TextArea();
        vBox0 = new VBox();
        label0 = new Label();
        filesTable = new FilesTable();
        downloadBtn = new Button();
        label1 = new Label();
        dragAndDropPane = new StackPane();

        setId("AnchorPane");

        hBox.setSpacing(20.0);

        label.setText("Clients:");

        clientsTable.setPrefHeight(274.0);
        clientsTable.setPrefWidth(296.0);

        logLabel.setText("Log:");
        VBox.setMargin(logLabel, new Insets(20.0, 0.0, 0.0, 0.0));

        logTextArea.setEditable(false);
        logTextArea.setPrefHeight(200.0);
        logTextArea.setPrefWidth(200.0);

        label0.setText("Files:");

        filesTable.setPrefHeight(274.0);
        filesTable.setPrefWidth(550.0);

        downloadBtn.setAlignment(javafx.geometry.Pos.CENTER);
        downloadBtn.setMnemonicParsing(false);
        downloadBtn.setText("Download");
        VBox.setMargin(downloadBtn, new Insets(20.0, 0.0, 0.0, 0.0));

        label1.setText("Drag your file here:");
        VBox.setMargin(label1, new Insets(20.0, 0.0, 0.0, 0.0));

        dragAndDropPane.setPrefHeight(150.0);
        dragAndDropPane.setPrefWidth(200.0);
        dragAndDropPane.setStyle("-fx-border-color: lightgray;");
        VBox.setMargin(dragAndDropPane, new Insets(0.0));
        hBox.setPadding(new Insets(20.0));

        vBox.getChildren().add(label);
        vBox.getChildren().add(clientsTable);
        vBox.getChildren().add(logLabel);
        vBox.getChildren().add(logTextArea);
        hBox.getChildren().add(vBox);
        vBox0.getChildren().add(label0);
        vBox0.getChildren().add(filesTable);
        vBox0.getChildren().add(downloadBtn);
        vBox0.getChildren().add(label1);
        vBox0.getChildren().add(dragAndDropPane);
        hBox.getChildren().add(vBox0);
        getChildren().add(hBox);

        //------HANDLERS------//
        dragAndDropPane.setOnDragOver((e) -> {
            dragAndDropPaneOnDragOverHandler(e);
        });

        dragAndDropPane.setOnDragDropped((e) -> {
            dragAndDropPaneOnDragDroppedHandler(e);
        });

        dragAndDropPane.setOnDragExited((e) -> {
            dragAndDropPaneOnDragExitedHandler(e);
        });

        clientsTable.setOnMouseReleased((e) -> {
            clientsTableOnMouseReleasedHandler(e);
        });

        downloadBtn.setOnAction((e) -> {
            downloadBtnOnActionHandler(e);
        });
    }

    private static void dragAndDropPaneOnDragOverHandler(DragEvent e) {
        final Dragboard db = e.getDragboard();
        if (db.hasFiles()) {
            dragAndDropPane.setStyle("-fx-border-color: red;"
                    + "-fx-border-width: 5;"
                    + "-fx-background-color: #C6C6C6;"
                    + "-fx-border-style: solid;");
            e.acceptTransferModes(TransferMode.COPY);
        } else {
            e.consume();
        }
    }

    private static void dragAndDropPaneOnDragDroppedHandler(DragEvent e) {
        Client selectedClient = clientsTable.getSelected();
        if (selectedClient == null) {
            addLogEntry("Please, select a client first");
            return;
        }
        final Dragboard db = e.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            e.acceptTransferModes(TransferMode.COPY);
            success = true;
            final File file = db.getFiles().get(0);
            new Thread() {
                public void run() {
                    TCPConnection.sendGiveMessageToAddress(selectedClient.getAddress(), file.getAbsolutePath());
                }
            }.start();

        }
        e.setDropCompleted(success);
        e.consume();
    }

    private static void dragAndDropPaneOnDragExitedHandler(DragEvent e) {
        dragAndDropPane.setStyle("-fx-border-color: lightgray;");
    }

    private static void clientsTableOnMouseReleasedHandler(Event e) {
        Client selected = clientsTable.getSelected();
        if (selected == null) {
            return;
        }
        UDPConnection.sendListRequestToAddress(selected.getAddress());
    }

    public static void clientsTableAddClient(Client client) {
        clientsTable.deleteClient(client.getName());
        clientsTable.addClient(client);
    }

    public static void clientsTableDeleteClient(String name) {
        clientsTable.deleteClient(name);
    }

    public static void filesTableSetFiles(List<FileBean> files) {
        filesTable.setFiles(files);
    }

    public static String clientsTableGetNameFromAddress(InetAddress addr) {
        return clientsTable.getNameFromAddress(addr);
    }

    public static void downloadBtnOnActionHandler(Event e) {
        FileBean selectedFile = filesTable.getSelected();
        if (selectedFile == null) {
            return;
        }
        Client selectedClient = clientsTable.getSelected();
        if (selectedClient == null) {
            return;
        }
        new Thread() {
            public void run() {
                TCPConnection.sendWantMessageToAddress(selectedClient.getAddress(), selectedFile.getPath());
            }
        }.start();

    }

    public static void addLogEntry(String entry) {
        synchronized (logTextArea) {
            logTextArea.setText(logTextArea.getText() + entry + "\n");
        }
    }

    public static void setLogEntry(String entry) {
        synchronized (logTextArea) {
            logTextArea.setText(entry + "\n");
        }
    }

    public static void clearLog(String entry) {
        synchronized (logTextArea) {
            logTextArea.setText("\n");
        }
    }

    public static void setupLogProgress() {
        synchronized (logTextArea) {
            logTextArea.setText(logTextArea.getText() + "Status of the transfer: 0%" + "\n");
        }

    }

    public static void advanceLogProgress(int value) {
        synchronized (logTextArea) {
            String[] lines = logTextArea.getText().split("\n");
            for (int i = lines.length - 1; i >= 0; i--) {
                if (lines[i].contains("%")) {
                    lines[i] = "Status of the transfer: " + value + "%\n";
                    break;
                }
            }
            String logRebuild = String.join("\n", lines);
            logTextArea.setText(logRebuild);
        }
    }
}
