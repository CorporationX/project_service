package faang.school.projectservice.exception;

/**
 * @author Alexander Bulgakov
 */

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
