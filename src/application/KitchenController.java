package application;

import java.io.IOException;

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
	
    @FXML
    private Button newKitchen;

    @FXML
    private RadioButton kitchen1;

    @FXML
    private ToggleGroup dinnerClub;

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
			((Node) event.getSource()).getScene().getWindow().hide();
			Parent root = FXMLLoader.load(getClass().getResource("/application/CreateDinnerClub.fxml"));
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
    void selectDinnerClub(ActionEvent event) {

    }
    
    ////////////////////////
    // Create new kitchen
    
    @FXML
    private Button newClub;

    @FXML
    private TextField newKitchenName;

    @FXML
    void newClubButtonClicked(ActionEvent event) {

    }
    
    @FXML
    private Button backButton;

    @FXML
    void backToKitchenFrame(ActionEvent event) {
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

    }


	
}
