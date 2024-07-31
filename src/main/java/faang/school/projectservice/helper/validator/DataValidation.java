package faang.school.projectservice.helper.validator;

import faang.school.projectservice.helper.exeption.NotNullProvidedException;
import org.springframework.stereotype.Component;

@Component
public class DataValidation {

    public <T> void checkOneTypeToNull(T value) {
        if (value == null) {
            throw new NotNullProvidedException();
        }
    }
}
