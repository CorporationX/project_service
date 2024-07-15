package faang.school.projectservice.exception;

public class StageException extends RuntimeException{

    public StageException(ErrorMessage message){
        super(message.getMessage());
    }

    public StageException(String message){
        super(message);
    }
}
