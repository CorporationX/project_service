package faang.school.projectservice.service.exception.notFoundException.internship;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class InternshipNotFoundException extends EntityNotFoundException {
    public InternshipNotFoundException(String message) {
        super(message);
    }
}