package com.revolhope.deepdev.tcpclient.controllers;

import java.io.File;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class MainController {

	
	@FXML private ListView<String> listViewToSend;
	@FXML private ListView<String> listViewConnDev;
	
	@FXML private Button btBrowse;
	@FXML private Button btSend;
	
	@FXML private Label labelRefreshTimestamp;
	@FXML private Label labelInfoFileSelected;
	
	@FXML private Pane paneDragDrop; 
	
	public void initialize() 
	{
		
		
		
		paneDragDrop.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                paneDragDrop.getScene().setCursor(Cursor.CLOSED_HAND);
                
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                    
                } else {
                    event.consume();
                }
            }
        });
        
		paneDragDrop.setOnDragExited(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				
				paneDragDrop.getScene().setCursor(Cursor.DEFAULT);
				
			}
			
		});
		
        // Dropping over surface
		paneDragDrop.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        System.out.println(filePath);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
		
	}
}
