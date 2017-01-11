package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LoginController {
	
    @FXML
    private Button newUser;

    @FXML
    private Button login;

    @FXML
    private TextField username;


    @FXML
    void newUser(ActionEvent event) {
    	System.out.println("newUser");
    }

    @FXML
    void login(ActionEvent event) {
    	String user = username.getText();
    	if(user.equals("Emilie")){
    		System.out.println("Welcome "+user);
    	} else {
    		System.out.println("wrong");
    	}
    	

    }
	
}
