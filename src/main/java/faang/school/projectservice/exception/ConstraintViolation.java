package faang.school.projectservice.exception;

public class ConstraintViolation extends RuntimeException{
    public ConstraintViolation (MessageError message){
        super(message.getMessage());
    }
    public ConstraintViolation(String message){
        super(message);
    }
}
