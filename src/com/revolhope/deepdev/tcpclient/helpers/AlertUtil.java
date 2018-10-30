package com.revolhope.deepdev.tcpclient.helpers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtil 
{
	
	private static Alert alert;
	
	public static void show(AlertType type, String title, String header, String msg)
	{
		alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(msg);
		alert.show();
	}
	
	public static void showDialog()
	{
		
	}
}
