package faang.school.projectservice.exception.notFoundException.resource;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
