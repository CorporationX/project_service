package faang.school.projectservice.service.commonServiceMethods;

import com.amazonaws.services.kms.model.NotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public class CommonMethods {
    public <T> T findEntityById(CrudRepository<T, Long> repository, long id, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s with id %d not found", entityName, id)));
    }

    public <T extends Enum<T>> T findValueInEnum(String value, Class<T> enumClass, String entityName) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(entityName + " with name: " + value + " not found in enum " + enumClass.getSimpleName());
        }
    }
}
