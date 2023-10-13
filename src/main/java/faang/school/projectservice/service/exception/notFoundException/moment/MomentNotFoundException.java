package faang.school.projectservice.service.exception.notFoundException.moment;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class MomentNotFoundException extends EntityNotFoundException {
    public MomentNotFoundException(String message) {
        super(message);
    }
}