package faang.school.projectservice.exception.resource;

public class S3ServiceException extends RuntimeException {
    public S3ServiceException(Exception exception) {
        super(exception);
    }
}
