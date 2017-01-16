package application;

import java.io.IOException;

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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class BudgetController {
	private User user;
	
	

    @FXML
    private Label titleLabel;

    @FXML
    private Button logOutButton;

    @FXML
    private Button backButton;

    @FXML
    private Label balanceLabel;

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
    	newScene(event,"/application/DayOverview.fxml");
    }

    @FXML
    void logOutButtonClicked(ActionEvent event) {

    }
	
	
	//////////////////////////////
    // controller methods
    
    public void setBalance(){
    	balanceLabel.setText("test");
    	
    	user.command("getBalance", 0, 0, 0, 0);
    	if(user.getFeedbackMsg() != null){
    		
    		balanceLabel.setText(user.getFeedbackMsg());
    	} else {
    		balanceLabel.setText("none");
    	}
    	balanceLabel.setText("test");
    }
    
    
    public void setUser(User user){
    	this.user = user;
    }

    
	@SuppressWarnings("unused")
	private void newScene(ActionEvent event, String path) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		int x = 400;
		System.out.println("budgetController: "+user.getUserName());
		
		switch (path){
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			loginController.setUser(new User());
			break;
			
		case "/application/DayOverview.fxml":
			DayController dayController = loader.getController();
			dayController.setUser(user);
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