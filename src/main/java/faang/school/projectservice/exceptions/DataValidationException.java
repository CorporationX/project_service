package faang.school.projectservice.exceptions;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String name){
        super(name);
    }

    public DataValidationException(MessageError error){
        super(error.getMessage());
    }
}
