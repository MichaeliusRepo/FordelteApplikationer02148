package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    void login(ActionEvent event) {
    	String user = username.getText();
    	
    	if(DinnerClub.getUser(user) != null){
    		wrongUsername.setText("");
    		System.out.println("Welcome "+user);
    		// new window
    	} else {
    		wrongUsername.setText("  Unknown username");
    		System.out.println("wrong");
    		// unknown user
    	}
    }
	
}
