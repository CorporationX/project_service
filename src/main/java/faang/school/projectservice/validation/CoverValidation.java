package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.web.multipart.MultipartFile;

public class CoverValidation {

    private static final long MAX_FILE_SIZE =  5 * 1024 * 1024;

    public static void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new DataValidationException("File size exceeds 5MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new DataValidationException("File must be an image");
        }
    }
}