package com.revolhope.deepdev.tcpclient.main;
	
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader();
			URL url = getClass().getClassLoader().getResource("com/revolhope/deepdev/tcpclient/views/MyFTP_ConfigView.fxml");
	        loader.setLocation(url);
	        Parent root = (Parent)loader.load();
	        Scene scene = new Scene(root);
	        
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("FTP");
	        primaryStage.setResizable(false);
	        primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
