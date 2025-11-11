package faang.school.projectservice.exception;

public class InternshipAlreadyCompletedException extends RuntimeException {
    public InternshipAlreadyCompletedException() {
        super("Finished internship can't be update");
    }
}
