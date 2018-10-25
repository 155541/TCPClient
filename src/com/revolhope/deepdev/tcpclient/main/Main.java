package com.revolhope.deepdev.tcpclient.main;
	
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	private static final String pathConfigView = "com/revolhope/deepdev/tcpclient/views/MyFTP_ConfigView.fxml";
	private static final String pathMainView = "com/revolhope/deepdev/tcpclient/views/MyFTP_MainView.fxml";
	
	private static final String pathConfigFile = ".ftp-server_config.txt";
	
	@Override
	public void start(Stage primaryStage) 
	{
		try {		
			URL url;
			String title = "";
			FXMLLoader loader = new FXMLLoader();
			
			if (existsConfigFile())
			{
				url = getClass().getClassLoader().getResource(pathMainView);
				title = "LocalShare Client";
			}
			else
			{
				url = getClass().getClassLoader().getResource(pathConfigView);
				title = "Configure";
			}
			
	        loader.setLocation(url);
	        Parent root = (Parent)loader.load();
	       	Scene scene = new Scene(root);
	        primaryStage.setTitle(title);
	        primaryStage.setScene(scene);
	        primaryStage.setResizable(false);
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
	
	public void loadMainView(Stage primaryStage)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
			URL url = getClass().getClassLoader().getResource(pathMainView);
			
			loader.setLocation(url);
	        Parent root = (Parent)loader.load();
	        Scene scene = new Scene(root);
	        
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("LocalShare Client");
	        primaryStage.setResizable(false);
	        primaryStage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean existsConfigFile()
	{
		File f = new File(pathConfigFile);
		return f.exists() && !f.isDirectory();
	}
}
