package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

import classes.Server;

public class LoginController {
	Server DinnerClub = new Server();
	
    @FXML
    private Button newUser;

    @FXML
    private Button login;

    @FXML
    private TextField username;
    
    @FXML
    private Label wrongUsername;


    @FXML
    void newUser(ActionEvent event) {
    	
    	System.out.println("newUser");
    }

    @FXML
    void login(ActionEvent event) throws IOException {
    	String user = username.getText();
    	
    	if(DinnerClub.getUser(user) != null){
    		wrongUsername.setText("");
    		System.out.println("Welcome "+user);
    //		Parent create_club_page = FXMLLoader.load(getClass().getResource("CreateDinnerClub.fxml"));
    	//	Scene home_page_scene = new Scene(create_club_page);
    		// new window
    	} else {
    		wrongUsername.setText("  Unknown username");
    		System.out.println("wrong");
    		// unknown user
    	}
    }
	
}
