package faang.school.projectservice.exeption;

import org.springframework.http.HttpStatus;

public class FileUploadException extends ApiException {

    public FileUploadException(String message) {
        super(message, message);
    }

    public FileUploadException(String message, String debugMessage) {
        super(message, debugMessage);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}