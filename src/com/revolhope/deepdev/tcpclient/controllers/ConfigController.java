package com.revolhope.deepdev.tcpclient.controllers;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import com.revolhope.deepdev.tcpclient.helpers.Toolkit;
import com.revolhope.deepdev.tcplibrary.constants.Params;
import com.revolhope.deepdev.tcplibrary.helpers.TcpClient;
import com.revolhope.deepdev.tcplibrary.model.Device;
import com.revolhope.deepdev.tcplibrary.model.Header;
import com.revolhope.deepdev.tcplibrary.model.Packet;
import com.revolhope.deepdev.tcplibrary.model.Code;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class ConfigController 
{
	private URL urlMainView;
	
	@FXML private TextField textFieldDeviceName;
	@FXML private TextField textFieldDeviceHomeDirectory;
	
	@FXML private Button btBrowse;
	@FXML private Button btDone;
	@FXML private Button btCancel;
	
	public void initialize()
	{
		
		btBrowse.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				DirectoryChooser chooser = new DirectoryChooser();
				
				File selectedDirectory = chooser.showDialog(btBrowse.getScene().getWindow());
				
				if (selectedDirectory != null)
				{
					textFieldDeviceHomeDirectory.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});
		
		btDone.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if (checkFields())
				{
					try 
					{
						Packet packet = new Packet();
						Header header = new Header();
						
						header.setCode(Code.REQ_INIT);
						header.setTimestamp(System.currentTimeMillis());
						header.setToken(null);
						
						Device device = new Device();
						device.setName(textFieldDeviceName.getText());
						device.setCreatedDate(System.currentTimeMillis());
						device.setCurrentInetAddress(InetAddress.getLocalHost());
						device.setMacAddress(Toolkit.getMacAddress());
						
						packet.setHeader(header);
						packet.setBody(device);
						System.out.println("packet send");
						
						TcpClient.send( packet, 
										InetAddress.getByName(Params.SERVER_ADDRESS),
										Params.PORT,
										new TcpClient.OnResponse() 
						{
							
							@Override
							public void responseReceived(Packet packet) 
							{
								if (packet != null)
								{
									Header header = packet.getHeader();
									Device device = (Device) packet.getBody();
									switch (header.getCode()) {
										case RES_OK:
											
											Toolkit.writeConfigFile(device.getName(), textFieldDeviceHomeDirectory.getText());
											break;
										
										case RES_ERROR_SQL:
											
										default:
											break;
									}
								}
								System.out.println(packet == null ? "Packet is null" : "Server says: "+packet.getHeader().getCode());
							}
						});
						
						
					} 
					catch (/*IO*/Exception e) 
					{
						e.printStackTrace();
					} 
				}
				else
				{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Fields wrong filled");
					alert.show();
				}
			}
		});
		
		btCancel.setOnAction(new EventHandler<ActionEvent>()
		{	
			@Override
			public void handle(ActionEvent event)
			{
				System.exit(0);
			}
		});
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkFields()
	{
		boolean ok = true;
		
		if (textFieldDeviceName.getText() == null || textFieldDeviceName.getText().isEmpty())
		{
			ok = false;
		}
		if (textFieldDeviceHomeDirectory.getText() == null || textFieldDeviceHomeDirectory.getText().isEmpty())
		{
			ok = false;
		}
		
		return ok;
	}
	
	/**
	 * 
	 * @param urlMainView
	 */
	public void setUrlMainView(URL urlMainView)
	{
		this.urlMainView = urlMainView;
	}
}
