package sample.dataClasses;

/**
 Это исключение выбрасывается, если в форму введено недостаточно данных
 */

public class NEDException extends Exception {
    public NEDException() {
        super("Print the text into the form!");
    }

    public NEDException(int n) {
        super("Print the deadline date!");
    }
}
