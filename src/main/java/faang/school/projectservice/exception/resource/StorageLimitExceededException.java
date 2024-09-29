package faang.school.projectservice.exception.resource;

import faang.school.projectservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class StorageLimitExceededException extends ApiException {
    private static final String MESSAGE = "The project has run out of free space";

    public StorageLimitExceededException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
