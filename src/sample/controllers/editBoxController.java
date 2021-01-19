package sample.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.dataClasses.NEDException;
import sample.dataClasses.Task;
import sample.dbpackage.DatabaseHandler;

public class editBoxController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField taskTextField;

    @FXML
    private DatePicker dateField;

    @FXML
    private ChoiceBox<Integer> priorityField;

    @FXML
    private TextField projectTextField;

    @FXML
    private TextField personTextField;

    @FXML
    private TextField noteTextField;

    @FXML
    private Button submitAddButton;

    @FXML
    private Label alertMessage;

    @FXML
    Task oldItem = mainWindowController.taskSelected;

    @FXML
    void initialize() {
        ObservableList<Integer> priority = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        priorityField.setItems(priority);

        initOldData();

        submitAddButton.setOnAction(e -> {
            try {
                if (taskTextField.getText().length() == 0)
                    throw new NEDException();
                else if (dateField.getValue() == null)
                    throw new NEDException(1);

                editSelTask();
                Stage stage = (Stage) submitAddButton.getScene().getWindow();
                stage.close();
            } catch (ParseException ex) {
                ex.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (NEDException ex) {
                alertMessage.setText(ex.getMessage());
                System.out.println(ex.getMessage());
            }
        });
    }

    private void initOldData() {
        taskTextField.setText(oldItem.getTaskText());
        priorityField.setValue(oldItem.getPriority());
        projectTextField.setText(oldItem.getProject());
        noteTextField.setText(oldItem.getNote());
    }

    private void editSelTask() throws ParseException, SQLException, ClassNotFoundException {
        Task newItem = new Task();
        Integer[] ID = new Integer[2];
        DatabaseHandler dbHandler = new DatabaseHandler();

        ID = dbHandler.findData(oldItem);

        newItem.setTaskText(taskTextField.getText());
        String dateStr = dateField.getValue().toString();
        String parsedStr = dateStr.substring(8, 10) + "." + dateStr.substring(5, 7) + "." + dateStr.substring(0, 4);
        try {
            newItem.setDate(parsedStr);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        newItem.setPriority(priorityField.getValue());
        newItem.setProject(projectTextField.getText());
        newItem.setNote(noteTextField.getText());
        newItem.setName(oldItem.getName());

        try {
            dbHandler.updateSelData(ID, newItem);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
