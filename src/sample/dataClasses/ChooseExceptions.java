package sample.dataClasses;

/**
 Это исключение выбрасывается, когда не был выделена запись для работы с ней
 */

public class ChooseExceptions extends Exception {
    public ChooseExceptions() {
        super("Choose the item to work");
    }
}
