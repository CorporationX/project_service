package faang.school.projectservice.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, long entityId) {
        super(String.format("%s with id: %d not found", entityName, entityId));
    }
}
