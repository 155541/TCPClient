package com.revolhope.deepdev.tcpclient.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.revolhope.deepdev.tcpclient.helpers.AlertUtil;
import com.revolhope.deepdev.tcpclient.helpers.ClientUtil;
import com.revolhope.deepdev.tcplibrary.constants.Params;
import com.revolhope.deepdev.tcplibrary.helpers.client.OnResponse;
import com.revolhope.deepdev.tcplibrary.helpers.client.TcpClient;
import com.revolhope.deepdev.tcplibrary.model.Code;
import com.revolhope.deepdev.tcplibrary.model.DataFile;
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
	@FXML private Button btRequest;
	
	@FXML private Label labelRefreshTimestamp;
	@FXML private Label labelInfoFileSelected;
	
	@FXML private Pane paneDragDrop; 
	
	
	private static ArrayList<Device> connDevices;
	private static ArrayList<File> filesToSend;
	private SimpleDateFormat sdf;
	
	
	public void initialize() 
	{
		obsListConnDev = FXCollections.observableArrayList();
		obsListToSend = FXCollections.observableArrayList();
		filesToSend = new ArrayList<>();
		
		/**
		 * Open session with server
		 */
		try 
		{
			openSession();
		} 
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
		
		/**
		 * Definition of the ContextMenus: ConnectedDevice ContextMenu & FilesToSend ContextMenu
		 */
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
					TcpClient.send(packet, InetAddress.getByName(Params.SERVER_ADDRESS), Params.PORT, new OnResponse() 
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
								labelRefreshTimestamp.setText("Last: " + getFormattedTimestamp());
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
		
		
		
		/**
		 * Pane DragAndDrop configuration
		 */
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
	
	
		/**
		 * Button send onAction definition
		 */
		btSend.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				ObservableList<Integer> indxs = listViewConnDev.getSelectionModel().getSelectedIndices();
				ObservableList<String> fileNames = listViewToSend.getSelectionModel().getSelectedItems();
				Device targetDev;
				String name, ext;
				String[] segments;
				DataFile df;
				
				for (int indx : indxs)
				{
					if (connDevices.size() > indx)
					{
						targetDev = connDevices.get(indx);
						ArrayList<DataFile> files = new ArrayList<>();
						
						for (String path : fileNames)
						{
							
							segments = path.split(File.pathSeparator);
							name = segments[segments.length-1].split(".")[0];
							ext = segments[segments.length-1].split(".")[1];
							
							df = new DataFile();
							
							df.setFilename(name);
							df.setExtension(ext);
							try 
							{
								df.setSource(IOUtils.toByteArray(new FileInputStream(path)));
							} 
							catch (IOException e) {
								
								e.printStackTrace();
							}
							df.setOriginId(ClientUtil.getDevice().getId());
							df.setTargetId(targetDev.getId());
							df.setTimestamp(ClientUtil.timestamp());
							files.add(df);
						}
						
						Packet packet = new Packet();
						Header header = new Header();
						
						header.setCode(Code.REQ_TRANSMISSION);
						header.setToken(ClientUtil.getToken());
						header.setDeviceId(ClientUtil.getDevice().getId());
						header.setTimestamp(ClientUtil.timestamp());
						
						packet.setHeader(header);
						packet.setBody(files.toArray(new DataFile[0]));
						
						try 
						{
							TcpClient.send(packet, ClientUtil.getServerAddr(), Params.PORT, new OnResponse()
							{
								@Override
								public void responseReceived(Packet packet) 
								{ 
									if (packet.getHeader().getCode() == Code.RES_OK)
									{
										AlertUtil.show(AlertType.INFORMATION, "Files send", "All files have been send", "Total files: " + files.size());
									}
								}
							});
						} 
						catch (ClassNotFoundException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
				
			}
		});

		/**
		 * Button request onAction definition
		 */
		btRequest.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				
			}
		});
	}
	
	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void openSession() throws ClassNotFoundException, UnknownHostException, IOException
	{
		
		Packet packet = new Packet();
		Header header = new Header();
		
		header.setCode(Code.REQ_OPEN_SESSION);
		header.setTimestamp(ClientUtil.timestamp());
		
		packet.setHeader(header);
		packet.setBody(ClientUtil.getMacAddress());
		
<<<<<<< HEAD
		TcpClient.send(packet, InetAddress.getByName(Params.SERVER_ADDRESS), Params.PORT, new OnResponse() 
=======
		TcpClient.send(packet, ClientUtil.getServerAddr(), Params.PORT, new OnResponse() 
>>>>>>> branch 'master' of https://github.com/155541/TCPClient.git
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
					labelRefreshTimestamp.setText("Last: " + getFormattedTimestamp());
				}
				else
				{
					AlertUtil.show(AlertType.ERROR, h.getCode().toString(), null, o.toString());
				}
			}
		});
	}
	
	/**
	 * 
	 */
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
	
	/**
	 * 
	 * @return
	 */
	private String getFormattedTimestamp()
	{
		if (sdf == null)
		{
			sdf = new SimpleDateFormat("hh:mm");
		}
		return sdf.format(new Date(ClientUtil.timestamp()));
	}
}
