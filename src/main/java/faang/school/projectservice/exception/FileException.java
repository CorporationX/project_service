package faang.school.projectservice.exception;

public class FileException extends RuntimeException{
    public FileException (MessageError message){
        super(message.getMessage());
    }
}
