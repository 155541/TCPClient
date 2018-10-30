package com.revolhope.deepdev.tcpclient.controllers;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.revolhope.deepdev.tcpclient.helpers.Toolkit;
import com.revolhope.deepdev.tcplibrary.constants.Params;
import com.revolhope.deepdev.tcplibrary.helpers.TcpClient;
import com.revolhope.deepdev.tcplibrary.model.Code;
import com.revolhope.deepdev.tcplibrary.model.Device;
import com.revolhope.deepdev.tcplibrary.model.Header;
import com.revolhope.deepdev.tcplibrary.model.Packet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	
	
	private static ArrayList<Device> connDevices;
	
	public void initialize() 
	{
		
		try 
		{
			openSession();
		} 
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
		
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
	
	private void openSession() throws ClassNotFoundException, UnknownHostException, IOException
	{
		
		Packet packet = new Packet();
		Header header = new Header();
		
		header.setCode(Code.REQ_OPEN_SESSION);
		header.setTimestamp(Toolkit.timestamp());
		
		packet.setHeader(header);
		packet.setBody(Toolkit.getMacAddress());
		
		TcpClient.send(packet, InetAddress.getByName(Params.SERVER_ADDRESS), Params.PORT, new TcpClient.OnResponse() 
		{
			@SuppressWarnings("unchecked")
			@Override
			public void responseReceived(Packet packet) 
			{
				Object o = packet.getBody();
				connDevices = (ArrayList<Device>) o;
				Toolkit.thisDevice = connDevices.get(0);
				setConnDevicesList();
			}
		});
	}
	
	private void setConnDevicesList()
	{
		ObservableList<String> obsrv = FXCollections.observableArrayList();
		for (Device dev : connDevices)
		{
			obsrv.add(connDevices.indexOf(dev), dev.getName() + " : " + dev.getCurrentInetAddress().toString()); // TODO: CHECK!
		}
		listViewConnDev.setItems(obsrv);
	}
}
