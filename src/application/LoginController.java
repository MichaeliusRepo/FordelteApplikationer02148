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
	private Server DinnerClub = new Server();

    @FXML
    private Button newUserButton;

    @FXML
    private Button login;

    @FXML
    private TextField username;
    
    @FXML
    private Label wrongUsername;


    @FXML
    void newUserButtonClicked(ActionEvent event) {
    	try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/NewUser.fxml"));
			Parent root = loader.load();
			
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
    }

    @FXML
    void loginButtonClicked(ActionEvent event) throws IOException {
    	String user = username.getText();
    	
    	if(DinnerClub.getUser(user) != null){
    		wrongUsername.setText("");
    		// new window
    		try {
    			((Node) event.getSource()).getScene().getWindow().hide();
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/KitchenFrame.fxml"));
    			Parent root = loader.load();
				KitchenController controller = loader.getController();
				controller.setServer(DinnerClub);
				controller.findUsersKitchens(user);
				
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
    
    ///////////////////////////////////
    // new user
    
    @FXML
    private TextField newUserName;

    @FXML
    private TextField kitchenName;

    @FXML
    private Button createNewUserButton;

    @FXML
    private Button backButton;

    @FXML
    void backButtonClicked(ActionEvent event) {
    	try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Login.fxml"));
			Parent root = loader.load();
			
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
    }

    @FXML
    void createNewUserButtonClicked(ActionEvent event) {
    	DinnerClub.newUser(newUserName.getText(), kitchenName.getText());
    	
    	try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Login.fxml"));
			Parent root = loader.load();
			wrongUsername.setText("Insert your new username");
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

    }
    

}
