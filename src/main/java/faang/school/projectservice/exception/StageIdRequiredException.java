package faang.school.projectservice.exception;

public class StageIdRequiredException extends RuntimeException {
    public StageIdRequiredException() {
        super("Stage ID is required for this operation.");
    }
}