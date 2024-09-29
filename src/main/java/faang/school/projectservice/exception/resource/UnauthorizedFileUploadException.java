package faang.school.projectservice.exception.resource;

import faang.school.projectservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UnauthorizedFileUploadException extends ApiException {
    private static final String MESSAGE = "You are not allowed to upload files to a project you don't own.";

    public UnauthorizedFileUploadException() {
        super(MESSAGE, HttpStatus.CONFLICT);
    }
}
