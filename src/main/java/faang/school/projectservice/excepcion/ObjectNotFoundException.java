package faang.school.projectservice.excepcion;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message, Exception cause) {
        super(message, cause);
    }
}
