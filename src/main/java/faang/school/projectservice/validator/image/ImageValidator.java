package faang.school.projectservice.validator.image;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageValidator {

    @Value("${image.cover.max-file-size}")
    private Long maxSize;

    public void validateMaximumSize(Long size) {
        if (size > maxSize) {
            throw new DataValidationException("Exceeding the maximum file size");
        }
    }
}
