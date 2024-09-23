package faang.school.projectservice.exceptions;

public class DataValidationException extends IllegalArgumentException {
    public DataValidationException(String message) {
        super(message);
    }
}

