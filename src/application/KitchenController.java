package application;

import java.io.IOException;

import classes.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class KitchenController {
	Server dinnerClub;
	
    @FXML
    private Button newKitchen;

    @FXML
    private RadioButton kitchen1;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private RadioButton kitchen2;

    @FXML
    private RadioButton kitchen3;

    @FXML
    private RadioButton kitchen4;

    @FXML
    private Button select;

    @FXML
    void newKitchen(ActionEvent event) {
    	try {
			newScene(event, "/application/CreateDinnerClub.fxml", user);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

	private void newScene(ActionEvent event, String path, String user) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		
		if(path.equals("/application/Login.fxml")){
			LoginController controller = loader.getController();
			controller.setServer(dinnerClub);
		} else if(path.equals("/application/KitchenFrame.fxml") ){
			KitchenController controller = loader.getController();
			controller.setServer(dinnerClub);
			controller.findUsersKitchens(user);
		} else if(path.equals("/application/CreateDinnerClub.fxml")){
			KitchenController controller = loader.getController();
			controller.setServer(dinnerClub);
			controller.setUser(user);
		}
		
		Scene scene = new Scene(root,400,400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Dinner Club");
		stage.show();
	}

    @FXML
    void selectDinnerClub(ActionEvent event) {
    	System.out.println(((RadioButton)toggleGroup.getSelectedToggle()).getText());
    	dinnerClub.getUser(user).setKitchen(((RadioButton)toggleGroup.getSelectedToggle()).getText());
    }
    
    ////////////////////////
    // Create new kitchen
    
    @FXML
    private Button newClub;

    @FXML
    private TextField newKitchenName;

    @FXML
    void newClubButtonClicked(ActionEvent event) {
    	if(!newKitchenName.getText().equals("")){
    		
    		System.out.println("new Kicthen creating: "+dinnerClub);
        	if(dinnerClub.addKitchen(newKitchenName.getText())){
        		System.out.println("A new kitchen has been created: "+newKitchenName.getText());
        	} else {
        		System.out.println("This kitchen already exist: "+newKitchenName.getText());
        	}
        	try {
        		dinnerClub.getUser(user).addKitchen(newKitchenName.getText());
				newScene(event,"/application/KitchenFrame.fxml", user);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
    	} else {
    		System.out.println("not valid: Remember to write a name");
    	}
    }
    
    @FXML
    private Button backButton;

	private String user;

    @FXML
    void backToKitchenFrame(ActionEvent event) {
		try {
			newScene(event,"/application/KitchenFrame.fxml", user);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    
    
    
    // Getting Server to call methods
    public void setServer(Server dinnerClub){
    	this.dinnerClub = dinnerClub;
    }
    
    public void setUser(String user){
    	this.user = user;
    }
    
    public void findUsersKitchens(String userName){
    	setUser(userName);
    	setRadioButtons(userName,kitchen1, 0);
    	setRadioButtons(userName,kitchen2, 1);
    	setRadioButtons(userName,kitchen3, 2);
    	setRadioButtons(userName,kitchen4, 3);
    }
    
    
    private void setRadioButtons(String userName, RadioButton kitchen, int i){
    	if(dinnerClub.getUser(userName).getKitchenName(i) != null){
    		kitchen.setText(dinnerClub.getUser(userName).getKitchenName(i));
    		kitchen.setVisible(true);
    	} else {
    		kitchen.setVisible(false);
    	}
    }


	
}
