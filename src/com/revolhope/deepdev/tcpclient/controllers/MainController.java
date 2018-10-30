package com.revolhope.deepdev.tcpclient.controllers;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.revolhope.deepdev.tcpclient.helpers.AlertUtil;
import com.revolhope.deepdev.tcpclient.helpers.ClientUtil;
import com.revolhope.deepdev.tcplibrary.constants.Params;
import com.revolhope.deepdev.tcplibrary.helpers.TcpClient;
import com.revolhope.deepdev.tcplibrary.model.Code;
import com.revolhope.deepdev.tcplibrary.model.Device;
import com.revolhope.deepdev.tcplibrary.model.Header;
import com.revolhope.deepdev.tcplibrary.model.Packet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class MainController {

	
	@FXML private ListView<String> listViewToSend;
	@FXML private ListView<String> listViewConnDev;
	private ObservableList<String> obsListConnDev;
	private ObservableList<String> obsListToSend;
	
	
	@FXML private Button btBrowse;
	@FXML private Button btSend;
	
	@FXML private Label labelRefreshTimestamp;
	@FXML private Label labelInfoFileSelected;
	
	@FXML private Pane paneDragDrop; 
	
	
	private static ArrayList<Device> connDevices;
	private static ArrayList<File> filesToSend;
	
	public void initialize() 
	{
		obsListConnDev = FXCollections.observableArrayList();
		obsListToSend = FXCollections.observableArrayList();
		filesToSend = new ArrayList<>();
		
		try 
		{
			openSession();
		} 
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
		
		final ContextMenu contextMenuConnDev = new ContextMenu();
		
		MenuItem item1 = new MenuItem("Refresh list");
		item1.setOnAction(new EventHandler<ActionEvent>() 
		{
		    public void handle(ActionEvent e)
		    {
		    	Packet packet = new Packet();
		    	Header header = new Header();
		    	
		    	header.setCode(Code.REQ_GET_DEV_CONN);
		    	header.setTimestamp(ClientUtil.timestamp());
		    	header.setDeviceId(ClientUtil.getDevice().getId());
		    	header.setToken(ClientUtil.getToken());
		    	
		    	packet.setHeader(header);
		    	try {
					TcpClient.send(packet, InetAddress.getByName(Params.SERVER_ADDRESS), Params.PORT, new TcpClient.OnResponse() 
					{
						@SuppressWarnings("unchecked")
						@Override
						public void responseReceived(Packet packet) 
						{
							Header h = packet.getHeader();
							Object o = packet.getBody();
							
							if (h.getCode() == Code.RES_OK)
							{
								connDevices = (ArrayList<Device>) o;
								setConnDevicesList();
								labelRefreshTimestamp.setText("Last: " +
										new SimpleDateFormat("hh:mm").format(new Date(ClientUtil.timestamp())));
							}
							else
							{
								AlertUtil.show(AlertType.ERROR, h.getCode().toString(), null, o.toString());
							}
						}
					});
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
		contextMenuConnDev.getItems().add(item1);
		listViewConnDev.setContextMenu(contextMenuConnDev);
		
		
		// TODO: Implement
		//final ContextMenu contextMenuFilesToSend = new ContextMenu();
		
		
		paneDragDrop.setOnDragOver(new EventHandler<DragEvent>() 
		{
            @Override
            public void handle(DragEvent event) 
            {
                Dragboard db = event.getDragboard();
                paneDragDrop.getScene().setCursor(Cursor.CLOSED_HAND);
                
                if (db.hasFiles()) 
                {
                    event.acceptTransferModes(TransferMode.COPY);
                } 
                else 
                {
                    event.consume();
                }
            }
        });
        
		paneDragDrop.setOnDragExited(new EventHandler<DragEvent>() 
		{
			@Override
			public void handle(DragEvent event) 
			{	
				paneDragDrop.getScene().setCursor(Cursor.DEFAULT);
			}
		});
		
		paneDragDrop.setOnDragDropped(new EventHandler<DragEvent>() 
		{
            @Override
            public void handle(DragEvent event) 
            {
                Dragboard dragboard = event.getDragboard();
                boolean success = false;
                
                if (dragboard.hasFiles()) 
                {    
                	success = true;                    
                    for (File file : dragboard.getFiles())
                    {
                        obsListToSend.add(file.getName());
                        filesToSend.add(file);
                        listViewToSend.setItems(obsListToSend);
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
		header.setTimestamp(ClientUtil.timestamp());
		
		packet.setHeader(header);
		packet.setBody(ClientUtil.getMacAddress());
		
		TcpClient.send(packet, InetAddress.getByName(Params.SERVER_ADDRESS), Params.PORT, new TcpClient.OnResponse() 
		{
			@SuppressWarnings("unchecked")
			@Override
			public void responseReceived(Packet packet) 
			{
				Header h = packet.getHeader();
				Object o = packet.getBody();
				
				if (h.getCode() == Code.RES_OK)
				{
					connDevices = (ArrayList<Device>) o;
					ClientUtil.setDevice(connDevices.get(0));
					ClientUtil.setToken(h.getToken());
					
					setConnDevicesList();
					labelRefreshTimestamp.setText("Last: " +
							new SimpleDateFormat("hh:mm").format(new Date(ClientUtil.timestamp())));
				}
				else
				{
					AlertUtil.show(AlertType.ERROR, h.getCode().toString(), null, o.toString());
				}
			}
		});
	}
	
	private void setConnDevicesList()
	{
		obsListConnDev.clear();
		for (Device dev : connDevices)
		{
			if (connDevices.indexOf(dev) != 0)
			{
				obsListConnDev.add(dev.getName() + " : " + dev.getCurrentInetAddress().toString()); // TODO: CHECK!
			}
		}
		listViewConnDev.setItems(obsListConnDev);
	}
}
