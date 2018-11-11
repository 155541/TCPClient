package com.revolhope.deepdev.tcpclient.helpers;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

public class AlertUtil 
{
	private static Alert alert;
	private static Optional<ButtonType> result;
	
	/**
	 * TODO: Java doc
	 * @param type
	 * @param title
	 * @param header
	 * @param msg
	 */
	public static void show(AlertType type, String title, String header, String msg)
	{
		alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(msg);
		alert.show();
	}
	
	/**
	 * TODO: Java doc
	 * @param type
	 * @param title
	 * @param header
	 * @param msg
	 * @param btTrue
	 * @param btFalse
	 * @return
	 */
	public static boolean showQuestion(AlertType type, String title, String header, String msg, String btTrue, String btFalse)
	{
		alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(msg);
		
		ButtonType buttonTrue = new ButtonType(btTrue == null ? "Ok" : btTrue);
		ButtonType buttonFalse = new ButtonType(btFalse == null ? "Cancel" : btFalse);
		
		alert.getButtonTypes().setAll(buttonFalse, buttonTrue);
		
		result = alert.showAndWait();
		return result.get() == buttonTrue;
	}
	
	/**
	 * TODO: Java doc
	 * @param deviceNames
	 */
	public static String showRequestDialog(List<String> deviceNames)
	{
		deviceNames.add(0, "All");
		ChoiceDialog<String> dialog = new ChoiceDialog<>("All", deviceNames);
		dialog.setTitle("Request");
		dialog.setHeaderText("Pick from which device wants to get pending files. Pick no one to get from all.");
		dialog.setContentText("Device name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
		    return result.get();
		}
		else
		{
			return null;
		}
	}
}
