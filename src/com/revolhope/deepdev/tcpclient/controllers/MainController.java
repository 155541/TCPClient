package com.revolhope.deepdev.tcpclient.controllers;

import java.io.File;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class MainController {

	@FXML private AnchorPane anchor; 
	
	public void initialize() {
		
		anchor.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                anchor.getScene().setCursor(Cursor.CLOSED_HAND);
                
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                    
                } else {
                    event.consume();
                }
            }
        });
        
		anchor.setOnDragExited(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				
				anchor.getScene().setCursor(Cursor.DEFAULT);
				
			}
			
		});
		
        // Dropping over surface
		anchor.setOnDragDropped(new EventHandler<DragEvent>() {
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
