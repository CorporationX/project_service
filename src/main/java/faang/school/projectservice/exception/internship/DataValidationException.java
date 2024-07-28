package faang.school.projectservice.exception.internship;

public class DataValidationException extends RuntimeException{

    public DataValidationException(InternshipError error) {
        super(error.getMessage());
    }
}
