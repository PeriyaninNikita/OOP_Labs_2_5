/**
 * Пользовательское исключение для ошибок работы с библиотечным каталогом.
 */
public class LibraryException extends Exception {
    /** Создает исключение с описанием причины ошибки. */
    public LibraryException(String message) { super(message); }
}