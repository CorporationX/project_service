package faang.school.projectservice.exception.resource;

import faang.school.projectservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ResourceDeleteNotAllowedException extends ApiException {
    private static final String MESSAGE = "You are not allowed to delete files from the project %s";

    public ResourceDeleteNotAllowedException(Long projectId) {
        super(MESSAGE, HttpStatus.CONFLICT, projectId);
    }
}
