package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ResourceValidator {

    public void validateResource(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DataValidationException("Загрузка невозможна: файл пустой");
        }
    }
}
