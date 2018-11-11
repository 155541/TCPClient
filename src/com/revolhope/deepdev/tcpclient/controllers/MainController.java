package com.revolhope.deepdev.tcpclient.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javafx.application.Platform;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {


	@FXML private MenuItem menuReconnect;
	@FXML private MenuItem menuCloseSession;
	@FXML private MenuItem menuRefreshDeviceList;
	@FXML private MenuItem menuClearFileList;
	@FXML private MenuItem menuRequest;
	@FXML private MenuItem menuSend;
	@FXML private MenuItem menuQuit;
	
	
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
		
		listViewConnDev.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewToSend.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		/**
		 * Menu's action definitions
		 */
		
		menuReconnect.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event) 
			{
				try 
				{
					openSession();
					AlertUtil.show(AlertType.CONFIRMATION, "Confirmation", null, "Connected with server");
				} 
				catch (ClassNotFoundException | IOException e) 
				{
					e.printStackTrace();
					AlertUtil.show(AlertType.ERROR, "Error", null, e.getMessage());
				}
			}
		});

		menuCloseSession.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				try
				{
					closeSession();
					AlertUtil.show(AlertType.CONFIRMATION, "Confirmation", null, "Session have been closed");
				}
				catch(Exception e)
				{
					AlertUtil.show(AlertType.ERROR, "Error", null, e.getMessage());
				}
			}
			
		});

		menuRefreshDeviceList.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event) 
			{
				refreshDeviceList();
			}
		});
		
		menuClearFileList.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				filesToSend.clear();
				obsListToSend.clear();
			}
		});

		menuRequest.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				try
				{
					request();
				}
				catch(Exception e)
				{
					AlertUtil.show(AlertType.ERROR, "Error", null, e.getMessage());
				}
			}
		});
		
		menuSend.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				send();
			}
		});
		
		menuQuit.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				quit();
			}
		});
		
		/**
		 * Handle window's close button click
		 */
		Stage stage = (Stage) btBrowse.getScene().getWindow();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() 
		{
			@Override
			public void handle(WindowEvent event) 
			{
				quit();
			}
		});
		
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
		MenuItem menuRefreshDeviceList = new MenuItem("Refresh list");
		menuRefreshDeviceList.setOnAction(new EventHandler<ActionEvent>() 
		{
		    public void handle(ActionEvent e)
		    {
		    	refreshDeviceList();
		    }
		});
		contextMenuConnDev.getItems().add(menuRefreshDeviceList);
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
				send();
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
				try
				{
					request();
				}
				catch(Exception e)
				{
					AlertUtil.show(AlertType.ERROR, "Error", null, e.getMessage());
				}
			}
		});
	}
	
	
	
	/**
	 * TODO
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
		
		TcpClient.send(packet, ClientUtil.getServerAddr(), Params.PORT, new OnResponse() 
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
	 * TODO
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws ClassNotFoundException 
	 */
	private void request() throws ClassNotFoundException, UnknownHostException, IOException 
	{
		Packet p = new Packet();
		Header h = new Header();
		
		h.setCode(Code.REQ_PENDING_FILES);
		h.setDeviceId(ClientUtil.getDevice().getId());
		h.setToken(ClientUtil.getToken());
		h.setTimestamp(ClientUtil.timestamp());
		
		p.setHeader(h);
		
		String selected = AlertUtil.showRequestDialog(obsListConnDev);
		if (selected != null)
		{
			p.setBody(null);
			for (Device d : connDevices)
			{
				if (d.getName().equals(selected))
				{
					p.setBody(d.getId());
					break;
				}
			}
			
			TcpClient.send(p, ClientUtil.getServerAddr(), Params.PORT, new OnResponse() 
			{
				@SuppressWarnings("unchecked")
				@Override
				public void responseReceived(Packet packet) 
				{
					Header h = packet.getHeader();
					Object o = packet.getBody();
					if (h.getCode() == Code.RES_OK)
					{
						File f;
						int count = 0;
						String path;
						FileOutputStream out;
						ArrayList<DataFile> files = (ArrayList<DataFile>) o;
						
						for (DataFile df : files)
						{
							path = ClientUtil.getHomePath() + File.separator + df.getFilename() + "." + df.getExtension();
							f = new File(path);
							
							try 
							{
								if (f.createNewFile())
								{
									out = new FileOutputStream(f);
									out.write(df.getSource());
									out.flush();
									out.close();
									count++;
								}
							}
							catch(IOException e)
							{
								AlertUtil.show(AlertType.ERROR, "Error", null, e.getMessage());
							}
						}
						
						AlertUtil.show(AlertType.INFORMATION, "Report", null, 
									   "Number of files received: " + files.size() + "\n" +
									   "Number of files writen: " + count);
						
					}
					else
					{
						AlertUtil.show(AlertType.ERROR, "Error", h.getCode().toString(), o.toString());
					}
				}
			});
			
		}
		else
		{
			AlertUtil.show(AlertType.ERROR, "Error", null, "Your choice was wrong.. please repeat it!");
		}
	}
	
	/**
	 * TODO
	 */
	private void send() 
	{
		ObservableList<Integer> indxs = listViewConnDev.getSelectionModel().getSelectedIndices();
		ObservableList<String> fileNames = listViewToSend.getSelectionModel().getSelectedItems();
		Device targetDev;
		String path, name, ext;
		String segment;
		DataFile df;
		
		for (int indx : indxs)
		{
			if (connDevices.size() > indx)
			{
				targetDev = connDevices.get(indx);
				ArrayList<DataFile> files = new ArrayList<>();
				
				for (String fn : fileNames)
				{
					path = null;
					for (File f : filesToSend)
					{
						if (f.getName().equals(fn)) { path = f.getAbsolutePath(); break; }
					}
					if (path != null)
					{
						segment = path.split(File.separator)[path.split(File.separator).length-1];
						String[] s = segment.split("\\.");
						name = s[0];
						ext = s[1];
						
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
	
	/**
	 * TODO
	 * @throws ClassNotFoundException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void closeSession() throws ClassNotFoundException, UnknownHostException, IOException 
	{
		Packet packet = new Packet();
		Header header = new Header();
		
		header.setCode(Code.REQ_CLOSE_SESSION);
		header.setTimestamp(ClientUtil.timestamp());
		
		packet.setHeader(header);
		packet.setBody(ClientUtil.getDevice());
		
		TcpClient.send(packet, ClientUtil.getServerAddr(), Params.PORT, new OnResponse() 
		{
			@Override
			public void responseReceived(Packet packet) 
			{
				Header h = packet.getHeader();
								
				if (h.getCode() == Code.RES_OK)
				{
					AlertUtil.show(AlertType.CONFIRMATION, "Confirmation", "Session have been closed", "If you need to connect again, please use Menu->Reconnect");
				}
				else
				{
					AlertUtil.show(AlertType.ERROR, h.getCode().toString(), null, packet.getBody().toString());
				}
			}
		});
	}
	
	/**
	 * TODO
	 */
	private void refreshDeviceList()
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
	
	/**
	 * TODO:
	 */
	private void setConnDevicesList()
	{
		obsListConnDev.clear();
		for (Device dev : connDevices)
		{
			if (dev.getId() != ClientUtil.getDevice().getId())
			{
				obsListConnDev.add(dev.getName() + " @ " + dev.getCurrentInetAddress().toString()); // TODO: CHECK!
			}
		}
		listViewConnDev.setItems(obsListConnDev);
	}
	
	/**
	 * TODO:
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
	
	/**
	 * TODO:
	 */
	private void quit() 
	{
		try
		{
			closeSession();
			Platform.exit();
		}
		catch(Exception e)
		{
			AlertUtil.show(AlertType.ERROR, "Error", null, e.getMessage());
			boolean result = AlertUtil.showQuestion(AlertType.ERROR,  "Error", null, e.getMessage(), "Exit anyway", null);
			if (result)
			{
				Platform.exit();
			}
		}
	}
}
