package faang.school.projectservice.exception;

public class ImageProcessingException extends RuntimeException {
    public ImageProcessingException(String message, Exception exception) {
        super(message, exception);
    }
}
