package faang.school.projectservice.exception.resource;

import faang.school.projectservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ResourceUploadException extends ApiException {
    private static final String MESSAGE = "Error while uploading file";

    public ResourceUploadException() {
        super(MESSAGE, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
