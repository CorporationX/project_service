package faang.school.projectservice.exception.notFoundException.moment;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class MomentNotFoundException extends EntityNotFoundException {
    public MomentNotFoundException(String message) {
        super(message);
    }
}