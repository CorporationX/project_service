package faang.school.projectservice.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Service
public class EntityUpdaterService {

    public <D, E> void updateNonNullFields(D source, E target) {
        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = ReflectionUtils.getField(field, source);

            if (value != null) {
                Field targetField = ReflectionUtils.findField(target.getClass(), field.getName());

                if (targetField != null) {
                    targetField.setAccessible(true);
                    ReflectionUtils.setField(targetField, target, value);
                }
            }
        }
    }
}
