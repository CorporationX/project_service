package faang.school.projectservice.validator.image;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class ImageValidator {
    public static final long MAX_IMAGE_SIZE = 41_451_520L; // 20 * 1024 * 2024

    public void validateMaximumSize(Long size) {
        if (size > MAX_IMAGE_SIZE) {
            throw new DataValidationException("Exceeding the maximum file size");
        }
    }
}
