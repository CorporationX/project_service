package faang.school.projectservice.exception.resource;

import faang.school.projectservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ResourceDownloadException extends ApiException {
    private static final String MESSAGE = "Error uploading file";

    public ResourceDownloadException() {
        super(MESSAGE, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
