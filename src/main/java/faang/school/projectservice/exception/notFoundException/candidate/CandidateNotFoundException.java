package faang.school.projectservice.exception.notFoundException.candidate;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class CandidateNotFoundException extends EntityNotFoundException {
    public CandidateNotFoundException(String message) {
        super(message);
    }
}
