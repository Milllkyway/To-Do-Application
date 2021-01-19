package sample.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.dataClasses.NEDException;
import sample.dataClasses.Task;
import sample.dbpackage.DatabaseHandler;

public class addPerformerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField taskTextField;

    @FXML
    private TextField personTextField;

    @FXML
    private Button submitAddButton;

    @FXML
    private Label alertMessage;

    @FXML
    Task oldItem = mainWindowController.taskSelected;

    @FXML
    void initialize() {
        taskTextField.setText(oldItem.getTaskText());
        taskTextField.setEditable(false);
        DatabaseHandler dbHandler = new DatabaseHandler();

        submitAddButton.setOnAction(e -> {
            try {
                if (personTextField.getText().length() == 0)
                    throw new NEDException();
                String newPerformer = personTextField.getText();
                dbHandler.addPerformer(newPerformer, oldItem);
                Stage stage = (Stage) submitAddButton.getScene().getWindow();
                stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (NEDException ex) {
                alertMessage.setText(ex.getMessage());
                System.out.println(ex.getMessage());
            }
            /*if (personTextField.getText().length() == 0) {
                alertMessage.setText("Print the new performer's name!");
            } else {
                String newPerformer = personTextField.getText();
                try {
                    dbHandler.addPerformer(newPerformer, oldItem);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                Stage stage = (Stage) submitAddButton.getScene().getWindow();
                stage.close();
            }*/
        });
    }
}
