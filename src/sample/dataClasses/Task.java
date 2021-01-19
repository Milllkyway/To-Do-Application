package sample.dataClasses;

import javafx.scene.control.CheckBox;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Task {
    public String taskText;
    public Date date;
    public String project;
    public String name;
    public int priority;
    public CheckBox check;
    public Boolean done;
    public String note;

    public Task() throws ParseException {
        this.taskText = "";
        this.date = parseDate("19.12.2000");
        this.project = "";
        this.name = "Milena";
        this.priority = 0;
        this.check = new CheckBox();
        this.done = false;
        this.note = "";
    }

    public Task(String taskText, String date, String project, String person_name, int priority, String note) throws ParseException {
        this.taskText = taskText;
        this.date = parseDate(date);
        this.project = project;
        this.name = person_name;
        this.priority = priority;
        this.check = new CheckBox();
        this.done = false;
        this.note = note;

        /*if (project == null) this.project = "";
        if (person_name == null) this.name = "";
        if (note == null) this.note = "";*/
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) throws ParseException {
        this.date = parseDate(date);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }


    public CheckBox getCheck() {
        /*if(this.check.isSelected()) {
            this.done = true;
        }
        System.out.println(this.done);*/
        return check;
    }

    public void setCheck(CheckBox check) {
        this.check = check;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date parseDate(String defDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        java.util.Date parsed = format.parse(defDate);

        Date date = new Date(parsed.getTime());
        return date;
    }

    /*public void checkBoxCheck() {
        if(this.check.isSelected()) {
            this.done = true;
        }
        System.out.println(this.done);
    }*/
}
