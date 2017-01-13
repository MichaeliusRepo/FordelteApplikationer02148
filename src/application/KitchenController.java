package application;

import java.io.IOException;

import classes.Server;
import classes.User;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class KitchenController {
	//private Server dinnerClub;
	private String username;
	private User user;
	
	
    ////////////////////////
    // Select kitchen
	
	@FXML
    public Label titleLabel;
    
	@FXML
    private ToggleGroup toggleGroup;

    @FXML
    private RadioButton kitchen1;

    @FXML
    private RadioButton kitchen2;

    @FXML
    private RadioButton kitchen3;

    @FXML
    private RadioButton kitchen4;

    @FXML
    private Button selectButton;
    
    @FXML
    private Button newKitchenButton;
    
    @FXML
    private Button logOutButton;

    @FXML
    void newKitchenButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/CreateDinnerClub.fxml");
    }

    @FXML
    void selectDinnerClubButtonClicked(ActionEvent event) throws IOException {
    	System.out.println(((RadioButton)toggleGroup.getSelectedToggle()).getText());
    	user.setKitchen(((RadioButton)toggleGroup.getSelectedToggle()).getText());
    	newScene(event,"/application/DayOverview.fxml");
    }
    
    @FXML
    void logOutButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/Login.fxml");
    }
    
    
    ////////////////////////
    // Create new kitchen
    
    @FXML
    private Button createKitchenButton;

    @FXML
    private TextField newKitchenTextField;
    
    @FXML
    private Button backButton;

    @FXML
    void createKitchenButtonClicked(ActionEvent event) throws IOException {
    	
    	if(!newKitchenTextField.getText().equals("")){
        	
    		if(user.addKitchen(newKitchenTextField.getText())){
        		System.out.println("A new kitchen has been created: "+newKitchenTextField.getText());
        	} else {
        		System.out.println("This kitchen already exist: "+newKitchenTextField.getText());
        	}
    		
			newScene(event,"/application/SelectKitchen.fxml");
			
    	} else {
    		System.out.println("not valid: Remember to write a name");
    	}
    }

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
		newScene(event,"/application/SelectKitchen.fxml");

    }
    
    //////////////////////////////
    // controller methods
   
    
    public void setUser(User user){
    	this.user = user;
    }
    
    public void findUsersKitchens(User user){
    	setUser(user);
    	setRadioButtons(kitchen1, 0);
    	setRadioButtons(kitchen2, 1);
    	setRadioButtons(kitchen3, 2);
    	setRadioButtons(kitchen4, 3);
    }
    
    private void setRadioButtons(RadioButton kitchen, int i){
    	if(user.getKitchenName(i) != null){
    		kitchen.setText(user.getKitchenName(i));
    		kitchen.setVisible(true);
    	} else {
    		kitchen.setVisible(false);
    	}
    }
    
	private void newScene(ActionEvent event, String path) throws IOException {
		int x = 400;
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		
		KitchenController kitchenController;
		
		
		switch (path){
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			loginController.setUser(user);
			break;
		
		case ("/application/SelectKitchen.fxml"):
			kitchenController = loader.getController();
			kitchenController.findUsersKitchens(user);
			break;
			
		case "/application/CreateDinnerClub.fxml":
			kitchenController = loader.getController();
			kitchenController.setUser(user);
			break;
			
		case "/application/DayOverview.fxml":
			DayController dayController = loader.getController();
			dayController.setUser(user);
			dayController.updateTabel(((RadioButton)toggleGroup.getSelectedToggle()).getText());
			x = 500;
			break;
		
		}
		
		Scene scene = new Scene(root,x,400);
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
