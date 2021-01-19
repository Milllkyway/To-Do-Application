package sample.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.dataClasses.ChooseExceptions;
import sample.dataClasses.Task;
import sample.dbpackage.DatabaseHandler;


public class mainWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> typeToSearch;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Task> toDoTable;

    @FXML
    private TableColumn<Task, String> taskColumn;

    @FXML
    private TableColumn<Task, String> dateColumn;

    @FXML
    private TableColumn<Task, String> projectColumn;

    @FXML
    private TableColumn<Task, String> personColumn;

    @FXML
    private TableColumn<Task, String> priorityColumn;

    @FXML
    private TableColumn<Task, String> doneColumn;

    @FXML
    private TableColumn<Task, String> notesColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button addPersonButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    /*@FXML
    private Button openButton;*/

    @FXML
    private Button saveButton;

    @FXML
    public static Task taskSelected;

    @FXML
    public ObservableList<Task> tasks = FXCollections.observableArrayList();

    @FXML
    void initialize() throws ParseException {
        initPart();

        addButton.setOnAction(e -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/views/addBox.fxml"));
            DatabaseHandler dbHandler = new DatabaseHandler();

            try {
                loader.load();
                Parent root = loader.getRoot();
                Stage window = new Stage();

                window.initModality(Modality.APPLICATION_MODAL);
                window.getIcons().add(new Image("/images/add.png"));
                window.setTitle("Adding new task");

                window.setScene(new Scene(root));
                window.showAndWait();

                tasks = dbHandler.getAllTasks();
                if (tasks != null) toDoTable.setItems(tasks);
            } catch (IOException | SQLException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            }
        });

        editButton.setOnAction(e -> {
            taskSelected = toDoTable.getSelectionModel().getSelectedItem();
            DatabaseHandler dbHandler = new DatabaseHandler();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/views/editBox.fxml"));

            try {
                if (taskSelected == null) throw new ChooseExceptions();
                loader.load();
                Parent root = loader.getRoot();
                Stage window = new Stage();

                window.initModality(Modality.APPLICATION_MODAL);
                window.getIcons().add(new Image("/images/edit.png"));
                window.setTitle("Edit the selected task");

                window.setScene(new Scene(root));
                window.showAndWait();

                tasks = dbHandler.getAllTasks();
                if (tasks != null) toDoTable.setItems(tasks);
            } catch (ChooseExceptions ex) {
                Optional<ButtonType> alert = new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                System.out.println("Item wasn't selected");
            } catch (IOException | SQLException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            }
        });

        addPersonButton.setOnAction(e -> {
            taskSelected = toDoTable.getSelectionModel().getSelectedItem();
            DatabaseHandler dbHandler = new DatabaseHandler();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/views/addPerformer.fxml"));
            try {
                if (taskSelected == null) throw new ChooseExceptions();
                loader.load();
                Parent root = loader.getRoot();
                Stage window = new Stage();

                window.initModality(Modality.APPLICATION_MODAL);
                window.getIcons().add(new Image("/images/group.png"));
                window.setTitle(" Add performer");

                window.setScene(new Scene(root));
                window.showAndWait();

                tasks = dbHandler.getAllTasks();
                if (tasks != null) toDoTable.setItems(tasks);
            } catch (IOException | SQLException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            } catch (ChooseExceptions ex) {
                Optional<ButtonType> alert = new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                System.out.println("Item wasn't selected");
            }
        });

        deleteButton.setOnAction(e -> {
            taskSelected = toDoTable.getSelectionModel().getSelectedItem();
            DatabaseHandler dbHandler = new DatabaseHandler();
            Integer[] ID;
            try {
                if (taskSelected == null) throw new ChooseExceptions();
                ID = dbHandler.findData(taskSelected);
                dbHandler.deleteSelData(ID);
                tasks = dbHandler.getAllTasks();
                if (tasks != null) toDoTable.setItems(tasks);
            } catch (SQLException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            } catch (ChooseExceptions ex) {
                Optional<ButtonType> alert = new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                System.out.println("Item wasn't selected");
            }
        });

        searchButton.setOnAction(e -> {
            DatabaseHandler dbHandler = new DatabaseHandler();
            try {
                tasks = dbHandler.searchBy(typeToSearch.getValue(), searchTextField.getText());
                searchTextField.clear();
            } catch (SQLException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            }
            toDoTable.setItems(tasks);
        });

        refreshButton.setOnAction(e -> {
            DatabaseHandler dbHandler = new DatabaseHandler();
            try {
                tasks = dbHandler.getAllTasks();
            } catch (SQLException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            }
            toDoTable.setItems(tasks);
        });

        saveButton.setOnAction(e -> {
            DatabaseHandler dbHandler = new DatabaseHandler();
            FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
            fileChooser.setTitle("Save Document");//Заголовок диалога
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");//Расширение
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = (Stage) saveButton.getScene().getWindow();;
            File file = fileChooser.showSaveDialog(stage);
            if(file == null) return; // Если пользователь нажал «отмена»
            else
                try {
                    BufferedWriter writer = new BufferedWriter (new FileWriter(file));
                    tasks = dbHandler.getAllTasks();;
                    for (Task task : tasks)
                        getTaskItems(writer, task);
                    writer.close();
                } catch (IOException | SQLException | ClassNotFoundException | ParseException ex) {
                    ex.printStackTrace();
                }
        });

        toDoTable.getSelectionModel().selectedItemProperty().addListener(e -> {
            DatabaseHandler dbHandler = new DatabaseHandler();
            taskSelected = toDoTable.getSelectionModel().getSelectedItem();
            if (taskSelected != null) {
                taskSelected.check.setOnAction( ev -> {
                    try {
                        if (taskSelected.check.isSelected()) {
                            System.out.println("HI");
                            dbHandler.doneTask(taskSelected, 1);
                        } else dbHandler.doneTask(taskSelected, 0);
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

    //@FXML
    void initPart() throws ParseException {
        DatabaseHandler dbHandler = new DatabaseHandler();
        // BUTTONS
        searchButton.setTooltip(new Tooltip("Write down the data and\n click the button to find it"));
        addButton.setTooltip(new Tooltip("Click the button \nto add task"));
        editButton.setTooltip(new Tooltip("Choose item and click the button \nto edit items"));
        addPersonButton.setTooltip(new Tooltip("Choose item and click the button \nto add the performer to the task"));
        deleteButton.setTooltip(new Tooltip("Choose item and click the button \nto delete it"));
        refreshButton.setTooltip(new Tooltip("Click the button \nto refresh the table"));
        //openButton.setTooltip(new Tooltip("Click the button \nto open file"));
        saveButton.setTooltip(new Tooltip("Click the button \nto save the list"));

        // TABLE
        taskColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("taskText"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("date"));
        projectColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("project"));
        personColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));
        doneColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("check"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("note"));

        // SEARCH
        ObservableList<String> typeToSearchValues = FXCollections.observableArrayList("Person", "Project", "Task", "Done");
        typeToSearch.setItems(typeToSearchValues);
        typeToSearch.setValue("Column");
        typeToSearch.setPadding(new Insets(0, 0, 0, 45));
        searchTextField.setPromptText("Value to search");

        try {
            tasks = dbHandler.getAllTasks();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        toDoTable.setItems(tasks);
    }

    void getTaskItems (BufferedWriter writer, Task task) {
        try {
            if (task.getTaskText().length() < 30)
                writer.write(task.getTaskText() + " ".repeat(30 - task.getTaskText().length()) + " | ");
            else writer.write(task.getTaskText() + " | ");

            writer.write(task.getDate().toString() + " | ");

            if (task.getProject().length() < 18)
                writer.write(task.getProject() + " ".repeat(18-task.getProject().length()) +" | ");
            else writer.write(task.getProject() +" | ");

            if (task.getName().length() < 10)
                writer.write(task.getName() + " ".repeat(10-task.getName().length()) + " | ");
            else writer.write(task.getName() + " | ");

            writer.write(task.getPriority() + " | ");

            if (task.getDone().toString() == "true") writer.write(task.getDone().toString() + "  | ");
            else writer.write(task.getDone().toString() + " | ");

            writer.write(task.note + " ");
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



