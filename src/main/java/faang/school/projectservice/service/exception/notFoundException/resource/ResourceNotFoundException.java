package faang.school.projectservice.service.exception.notFoundException.resource;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
