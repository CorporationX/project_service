package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ResourceValidator {
    @Value("${image.cover.maxSize}")
    private String maxSize;

    public void validateSizeFile(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new DataValidationException("File is null");
        }
        if (multipartFile.getSize() > Long.parseLong(maxSize)) {
            throw new DataValidationException("File size must be less than 5 MB");
        }
    }
}