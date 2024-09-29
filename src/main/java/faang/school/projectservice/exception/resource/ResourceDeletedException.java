package faang.school.projectservice.exception.resource;

import faang.school.projectservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ResourceDeletedException extends ApiException {
    private static final String MESSAGE = "Resource is deleted";

    public ResourceDeletedException() {
        super(MESSAGE, HttpStatus.CONFLICT);
    }
}
