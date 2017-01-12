package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


import classes.Server;

public class LoginController {
	private Server dinnerClub;
	private Stage stage;
	private Scene loginScene, newUserScene;

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
			newScene(event,"/application/NewUser.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void loginButtonClicked(ActionEvent event) throws IOException {
    	String user = username.getText();
    	
    	if(dinnerClub.getUser(user) != null){
    		wrongUsername.setText("");
    		// new window
    		try {
    			newScene(event,"/application/KitchenFrame.fxml");
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
			newScene(event, "/application/Login.fxml");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }



    @FXML
    void createNewUserButtonClicked(ActionEvent event) {
    	System.out.println(newUserName.getText()+" "+ kitchenName.getText());
    	System.out.println(""+dinnerClub);
    	if(!newUserName.getText().equals("") && !kitchenName.getText().equals("")){
    		dinnerClub.newUser(newUserName.getText(), kitchenName.getText());
    	} else {
    		System.out.println("Remember to insert kitchenName and Username");
    	}
    	
    	
    	try {
    		newScene(event,"/application/Login.fxml" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    //////////////////////////////
    // controller methods
    public void setServer(Server dinnerClub){
    	this.dinnerClub = dinnerClub;
    }

	private void newScene(ActionEvent event, String path) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		
		if(path.equals("/application/Login.fxml") || path.equals("/application/NewUser.fxml")){
			LoginController controller = loader.getController();
			controller.setServer(dinnerClub);
		} else if(path.equals("/application/KitchenFrame.fxml")){
			KitchenController controller = loader.getController();
			controller.setServer(dinnerClub);
			controller.findUsersKitchens(username.getText());
		}
		
		Scene scene = new Scene(root,400,400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
		});
		
		stage.setScene(scene);
		stage.setTitle("Dinner Club");
		stage.show();
	}
}
