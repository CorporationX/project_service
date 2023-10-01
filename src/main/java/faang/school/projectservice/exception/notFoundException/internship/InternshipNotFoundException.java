package faang.school.projectservice.exception.notFoundException.internship;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class InternshipNotFoundException extends EntityNotFoundException {
    public InternshipNotFoundException(String message) {
        super(message);
    }
}