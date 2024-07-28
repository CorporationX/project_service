package faang.school.projectservice.exception;

public class EntityNotFoundException extends jakarta.persistence.EntityNotFoundException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
