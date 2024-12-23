package faang.school.projectservice.exception;

public class SizeExceeded extends RuntimeException{
    public SizeExceeded(ErrorMessage message) {
        super(message.getMessage());
    }
}
