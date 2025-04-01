package faang.school.projectservice.exception;

public class LimitSizeFileException extends IllegalArgumentException {
    public LimitSizeFileException(String message) {
        super(message);
    }
}
