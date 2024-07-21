package faang.school.projectservice.exception;

public class DataValidationException extends RuntimeException{

    public DataValidationException(InternshipError error) {
        super(error.getMessage());
    }
}
