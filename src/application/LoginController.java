package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    		// new window
    		try {
    			((Node) event.getSource()).getScene().getWindow().hide();
				Parent root = FXMLLoader.load(getClass().getResource("/application/KitchenFrame.fxml"));
				Scene scene = new Scene(root,400,400);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.setTitle("Dinner Club");
				stage.show();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		wrongUsername.setText("  Unknown username");
    		System.out.println("wrong");
    	}
    }
    

}
