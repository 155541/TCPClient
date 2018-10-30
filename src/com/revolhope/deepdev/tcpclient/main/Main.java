package com.revolhope.deepdev.tcpclient.main;
	
import java.net.URL;

import com.revolhope.deepdev.tcpclient.controllers.ConfigController;
import com.revolhope.deepdev.tcpclient.helpers.Params;
import com.revolhope.deepdev.tcpclient.helpers.FileUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


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
				title = "LocalShare Client";
				
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
			FXMLLoader loader = new FXMLLoader();
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
}
