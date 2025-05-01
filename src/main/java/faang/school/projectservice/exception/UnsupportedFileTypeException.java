package faang.school.projectservice.exception;

public class UnsupportedFileTypeException extends IllegalArgumentException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
