package faang.school.projectservice.service.exception.notFoundException.candidate;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class CandidateNotFoundException extends EntityNotFoundException {
    public CandidateNotFoundException(String message) {
        super(message);
    }
}
