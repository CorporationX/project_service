package faang.school.projectservice.exception;

public class DatabaseCorruptedException extends RuntimeException {
    public DatabaseCorruptedException(String message) {
        super(message);
    }
}
