package faang.school.projectservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExceptionProcessor {

    public EntityNotFoundException throwEntityNotFoundException(Class<?> entityClass, long entityId){
        String errMessage = String.format("Could not find %s with ID: %d", entityClass.getName(), entityId);
        EntityNotFoundException exception = new EntityNotFoundException(errMessage);
        log.error(errMessage, exception);
        return exception;
    }
}
