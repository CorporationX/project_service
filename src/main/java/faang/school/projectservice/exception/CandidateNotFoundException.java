package faang.school.projectservice.exception;

public class CandidateNotFoundException extends RuntimeException {
    public CandidateNotFoundException(Long candidateId) {
        super("Candidate with id=%d not found".formatted(candidateId));
    }
}
