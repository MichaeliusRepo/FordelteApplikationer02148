package application;

import java.io.IOException;

import classes.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DayController {
	private Server dinnerClub;
	private String user;
	
	@FXML
	public Label titleLabel;

    @FXML
    private TableColumn<?, ?> dateTableColumn;

    @FXML
    private TableColumn<?, ?> chefTableColumn;

    @FXML
    private TableColumn<?, ?> attendTableColumn;

    @FXML
    private CheckBox viewPreviousCheckBox;

    @FXML
    private Button logOutButton;
    
    @FXML
    private Button backButton;

    @FXML
    private Button addDayButton;

    @FXML
    private Button budgetButton;
    
    @FXML
    private Button updateButton;

    @FXML
    void addDayButtonClicked(ActionEvent event) {

    }

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
    	newScene(event,"/application/SelectKitchen.fxml",user);
    }

    @FXML
    void bugdetButtonClicked(ActionEvent event) {

    }

    @FXML
    void logOutButtonClicked(ActionEvent event) throws IOException {
    	newScene(event,"/application/Login.fxml",user);
    }

    @FXML
    void viewPreviousCheckBoxClicked(ActionEvent event) {

    }
    
    @FXML
    void updateButtonClicked(ActionEvent event) {

    }
    
    //////////////////////////////
    // controller methods
    
    public void setServer(Server dinnerClub){
    	this.dinnerClub = dinnerClub;
    }
    
    public void setUser(String user){
    	this.user = user;
    }

    
	private void newScene(ActionEvent event, String path, String user) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		KitchenController kitchenController;
		
		switch (path){
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			loginController.setServer(dinnerClub);
			break;
		
		case ("/application/SelectKitchen.fxml"):
			kitchenController = loader.getController();
			kitchenController.setServer(dinnerClub);
			kitchenController.findUsersKitchens(user);
			break;
			
		case "/application/CreateDinnerClub.fxml":
			kitchenController = loader.getController();
			kitchenController.setServer(dinnerClub);
			kitchenController.setUser(user);
			break;
			
		case "/application/DayOverview.fxml":
			DayController dayController = loader.getController();
			dayController.setServer(dinnerClub);
			dayController.setUser(user);
			break;
		
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