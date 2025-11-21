package faang.school.projectservice.exception.internship;

public class AlreadyCompletedException extends RuntimeException {
    public AlreadyCompletedException() {
        super("Finished internship can't be update");
    }
}
