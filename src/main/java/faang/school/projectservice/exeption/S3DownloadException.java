package faang.school.projectservice.exeption;

import org.springframework.http.HttpStatus;

public class S3DownloadException extends ApiException {

    public S3DownloadException(String message) {
        super(message, message);
    }

    public S3DownloadException(String message, String debugMessage) {
        super(message, debugMessage);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}