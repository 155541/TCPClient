package com.revolhope.deepdev.tcpclient.main;
	
import java.net.URL;

import com.revolhope.deepdev.tcpclient.controllers.ConfigController;
import com.revolhope.deepdev.tcpclient.helpers.Params;
import com.revolhope.deepdev.tcpclient.helpers.AlertUtil;
import com.revolhope.deepdev.tcpclient.helpers.ClientUtil;
import com.revolhope.deepdev.tcpclient.helpers.FileUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;


public class Main extends Application {
	
	
	@Override
	public void start(Stage primaryStage) 
	{
		try {		
			URL urlMainView = getClass().getClassLoader().getResource(Params.pathMainView);
			
			Parent root;
			String title;
			FXMLLoader loader = new FXMLLoader();
			
			if (FileUtil.existsConfigFile())
			{
				
				String[] data = FileUtil.readConfigFile();
				if (data != null && data.length == 2)
				{
					title = "LocalShare -> Client: " + data[0];
					ClientUtil.setHomePath(data[1]);
				}
				else
				{
					AlertUtil.show(AlertType.WARNING,
								   "Caution",
							       "Error reading config file",
							       "You will not be able to download pending files...");
					title = "LocalShare - Client";
				}
				
				
				
				loader.setLocation(urlMainView);
				root = (Parent)loader.load();
			}
			else
			{
				title = "Configuration";
				
				loader.setLocation(getClass().getClassLoader().getResource(Params.pathConfigView));
				root = (Parent)loader.load();
				ConfigController configController = (ConfigController) loader.getController();
				configController.setUrlMainView(urlMainView);
				configController.setStage(primaryStage);
			}
			
	        
	       	Scene scene = new Scene(root);
	        primaryStage.setTitle(title);
	        primaryStage.setScene(scene);
	        primaryStage.setResizable(false);
	        primaryStage.initStyle(StageStyle.TRANSPARENT);
	        primaryStage.show();
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	public static void loadMainView(Stage primaryStage, URL url)
	{
		try
		{
			String title;
			String[] data = FileUtil.readConfigFile();
			
			if (data != null && data.length == 2)
			{
				title = "LocalShare -> Client: " + data[0];
				ClientUtil.setHomePath(data[1]);
			}
			else
			{
				AlertUtil.show(AlertType.WARNING,
							   "Caution",
						       "Error reading config file",
						       "You will not be able to download pending files...");
				title = "LocalShare - Client";
			}
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(url);
	        Parent root = (Parent)loader.load();
	        Scene scene = new Scene(root);
	        
	        primaryStage.setScene(scene);
	        primaryStage.setTitle(title);
	        primaryStage.setResizable(false);
	        primaryStage.initStyle(StageStyle.UTILITY);
	        primaryStage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
