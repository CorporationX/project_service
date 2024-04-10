package faang.school.projectservice.exception;

public class SizeExceeded extends RuntimeException{
    public SizeExceeded(MessageError message){
        super(message.getMessage());
    }
}
