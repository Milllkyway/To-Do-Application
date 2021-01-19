package sample.dbpackage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import sample.dataClasses.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.text.ParseException;

public class DatabaseHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + dbName + "?serverTimezone=Europe/Moscow&useSSL=false";

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString,
                dbUser, dbPass);

        return dbConnection;
    }

    public Integer addTaskToDB(Task newTask)  throws SQLException, ClassNotFoundException {
        ResultSet resSet = null;
        Integer newTaskId;
        String insertToTask = "INSERT INTO task(task_text, date, project_name, priority, note)" +
                              "VALUES(?, ?, ?, ?, ?)";
        String getTaskID = "SELECT * FROM task ORDER BY id_task DESC LIMIT 1";

        PreparedStatement prStTask = getDbConnection().prepareStatement(insertToTask);
        PreparedStatement prSt = getDbConnection().prepareStatement(getTaskID);

        prStTask.setString(1, newTask.getTaskText());
        prStTask.setDate(2, newTask.getDate());
        prStTask.setString(3, newTask.getProject());
        prStTask.setInt(4, newTask.getPriority());
        prStTask.setString(5, newTask.getNote());

        prStTask.executeUpdate();
        resSet = prSt.executeQuery();
        resSet.next();
        newTaskId = resSet.getInt(Const.TASK_ID);

        return newTaskId;
    }

    public Integer checkPersonInDB (String newPerson) throws SQLException, ClassNotFoundException {
        Integer idToAdd = 0;
        ResultSet resSet = null;

        String checkPerson = "SELECT * FROM person WHERE (name = ?)";
        PreparedStatement prSt = getDbConnection().prepareStatement(checkPerson);
        prSt.setString(1, newPerson);

        resSet = prSt.executeQuery();
        if (resSet.isBeforeFirst() ) {
            resSet.next();
            idToAdd = resSet.getInt(Const.PERSON_ID);
        }
        return idToAdd;
    }

    public Integer addPersonToDB (String newPerson) throws SQLException, ClassNotFoundException {
        Integer idToAdd;

        idToAdd = checkPersonInDB(newPerson);
        if (idToAdd == 0) {
            String insertToPerson = "INSERT INTO person(name) VALUES(?)";
            PreparedStatement prStPerson = getDbConnection().prepareStatement(insertToPerson);
            prStPerson.setString(1, newPerson);
            prStPerson.executeUpdate();
            idToAdd = checkPersonInDB(newPerson);
        }
        return idToAdd;
    }

    public void getConnToTables (Task newTask, String newPerson, Integer i) throws SQLException, ClassNotFoundException {
        Integer taskID, personID;
        String insert = "INSERT INTO project(id_person, id_task)\n" +
                "VALUES(?, ?)";

        if (i == 0) taskID = addTaskToDB(newTask);
        else taskID = i;

        personID = addPersonToDB(newPerson);

        System.out.println(taskID + " " + personID);

        PreparedStatement prSt = getDbConnection().prepareStatement(insert);
        prSt.setInt(1, personID);
        prSt.setInt(2, taskID);
        prSt.executeUpdate();
    }

    public Integer[] findData (Task selTask) throws SQLException, ClassNotFoundException {
        Integer[] ID = new Integer[2];
        ResultSet resSet = null;
        String findItem = "SELECT task.id_task, person.id_person FROM task \n" +
                "INNER JOIN project\n" +
                "ON task.id_task = project.id_task\n" +
                "LEFT JOIN person\n" +
                "ON project.id_person = person.id_person\n" +
                "WHERE task.task_text = ? AND person.name = ?";

        PreparedStatement prSt = getDbConnection().prepareStatement(findItem);
        prSt.setString(1, selTask.getTaskText());
        prSt.setString(2, selTask.getName());
        resSet = prSt.executeQuery();
        resSet.next();
        ID[0] = resSet.getInt("person.id_person");
        ID[1] = resSet.getInt("task.id_task");

        return ID;
    }

    public void deleteSelData (Integer[] ID)  throws SQLException, ClassNotFoundException {
        ResultSet resSetPerson, resSetTask = null;

        String deleteItem = "DELETE FROM project WHERE id_person = ? AND id_task = ?;\n";
        PreparedStatement prSt = getDbConnection().prepareStatement(deleteItem);
        prSt.setInt(1, ID[0]);
        prSt.setInt(2, ID[1]);
        prSt.executeUpdate();

        String findRelativePerson = "SELECT * FROM project WHERE id_person = ?;\n";
        PreparedStatement prStPerson = getDbConnection().prepareStatement(findRelativePerson);
        prStPerson.setInt(1, ID[0]);
        resSetPerson = prStPerson.executeQuery();
        if (!resSetPerson.isBeforeFirst() ) {
            resSetPerson.next();
            String deletePerson = "DELETE FROM person WHERE id_person = ?;\n";
            PreparedStatement prDelPerson = getDbConnection().prepareStatement(deletePerson);
            prDelPerson.setInt(1, ID[0]);
            prDelPerson.executeUpdate();
        }

        String findRelativeTask = "SELECT * FROM project WHERE id_task = ?;\n";
        PreparedStatement prStTask = getDbConnection().prepareStatement(findRelativeTask);
        prStTask.setInt(1, ID[1]);
        resSetTask = prStTask.executeQuery();
        if (!resSetTask.isBeforeFirst() ) {
            resSetTask.next();
            String deleteTask = "DELETE FROM task WHERE id_task = ?;\n";
            PreparedStatement prDelTask = getDbConnection().prepareStatement(deleteTask);
            prDelTask.setInt(1, ID[1]);
            prDelTask.executeUpdate();
        }
    }

    public void updateSelData (Integer[] ID, Task newTask)  throws SQLException, ClassNotFoundException {
        String update = "UPDATE task SET task_text = ?, date = ?, project_name = ?,\n" +
                        "priority = ?, note = ?\n" +
                        "WHERE id_task = ?";
        PreparedStatement prSt = getDbConnection().prepareStatement(update);
        prSt.setString(1, newTask.getTaskText());
        prSt.setDate(2, newTask.getDate());
        prSt.setString(3, newTask.getProject());
        prSt.setInt(4, newTask.getPriority());
        prSt.setString(5, newTask.getNote());
        prSt.setInt(6, ID[1]);
        prSt.executeUpdate();
    }

    public void doneTask (Task selTask, Integer i)  throws SQLException, ClassNotFoundException {
        Integer[] ID;
        ID = findData (selTask);

        String update = "UPDATE task SET done = ? WHERE id_task = ?";
        PreparedStatement prSt = getDbConnection().prepareStatement(update);
        prSt.setInt(1, i);
        prSt.setInt(2, ID[1]);
        prSt.executeUpdate();
    }

    public void addPerformer (String performer, Task item) throws SQLException, ClassNotFoundException {
        Integer[] ID = new Integer[2];
        ID = findData(item);
        getConnToTables(item, performer, ID[1]);
    }

    public ObservableList<Task> getAllTasks() throws SQLException, ClassNotFoundException, ParseException {
        ResultSet resSet = null;
        String select = "SELECT task.id_task, task.task_text, task.date, task.project_name,\n" +
                "person.name, task.priority, task.done, task.note\n" +
                "FROM task\n" +
                "\tINNER JOIN project\n" +
                "\tON task.id_task = project.id_task\n" +
                "\tLEFT JOIN person\n" +
                "\tON project.id_person = person.id_person";

        PreparedStatement prSt = getDbConnection().prepareStatement(select);
        resSet = prSt.executeQuery();
        ObservableList<Task> list = FXCollections.observableArrayList();

        while(resSet.next()){
            Task item = new Task();

            item.taskText = resSet.getString("task.task_text");
            item.date = resSet.getDate("task.date");
            item.project = resSet.getString("task.project_name");
            item.setName(resSet.getString("person.name"));
            item.priority = resSet.getInt("task.priority");
            item.check = new CheckBox();
            item.done = resSet.getBoolean("task.done");
            if (item.done == true)
                item.check.setSelected(true);
            item.note = resSet.getString("task.note");

            list.add(item);
        }
        System.out.println("All records have been selected");
        return list;
    }

    public ObservableList<Task> searchBy(String typeToSearch, String valueToSearch) throws SQLException, ClassNotFoundException, ParseException {
        ResultSet resSet = null;
        PreparedStatement prSt;
        Integer val;

        String select = "SELECT task.id_task, task.task_text, task.date, task.project_name,\n" +
                "person.name, task.priority, task.done, task.note\n" +
                "FROM task\n" +
                "\tINNER JOIN project\n" +
                "\tON task.id_task = project.id_task\n" +
                "\tLEFT JOIN person\n" +
                "\tON project.id_person = person.id_person\n";

        switch (typeToSearch) {
            case ("Person"):
                select +=  "\tWHERE (person.name = ?);";
                prSt = getDbConnection().prepareStatement(select);
                prSt.setString(1, valueToSearch);
                break;
            case ("Project"):
                select +=  "\tWHERE (task.project_name = ?)";
                prSt = getDbConnection().prepareStatement(select);
                prSt.setString(1, valueToSearch);
                break;
            case ("Task"):
                select +=  "\tWHERE (task.task_text = ?)";
                prSt = getDbConnection().prepareStatement(select);
                prSt.setString(1, valueToSearch);
                break;
            case ("Done"):
                select +=  "\tWHERE (task.done = ?)";
                prSt = getDbConnection().prepareStatement(select);
                if (valueToSearch.equals("done") || valueToSearch.equals("true") || valueToSearch.equals("1"))
                    prSt.setInt(1, 001);
                else
                    prSt.setInt(1, 000);
                break;
            default:
                prSt = getDbConnection().prepareStatement(select);
                break;
        }

        resSet = prSt.executeQuery();
        ObservableList<Task> list = FXCollections.observableArrayList();

        while(resSet.next()){
            Task item = new Task();

            item.taskText = resSet.getString("task.task_text");
            item.date = resSet.getDate("task.date");
            item.project = resSet.getString("task.project_name");
            item.setName(resSet.getString("person.name"));
            item.priority = resSet.getInt("task.priority");
            item.check = new CheckBox();
            item.done = resSet.getBoolean("task.done");
            if (item.done == true)
                item.check.setSelected(true);
            item.note = resSet.getString("task.note");

            list.add(item);
        }
        System.out.println("All records have been selected");
        return list;
    }
}
