package faang.school.projectservice.exception;

import com.amazonaws.AmazonServiceException;

public class MinioUploadException extends Exception {
    private final AmazonServiceException amazonServiceException;
    public MinioUploadException(String message, AmazonServiceException e) {
        super(message, e);
        this.amazonServiceException = e;
    }
    public AmazonServiceException getAmazonServiceException() {
        return amazonServiceException;
    }
}