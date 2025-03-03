package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidateException;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public static void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidateException("No file provided");
        }
        String mimeType = file.getContentType();
        if (mimeType == null || !mimeType.startsWith("image")) {
            throw new DataValidateException("File is not an image");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new DataValidateException("File size exceeds 5MB");
        }
    }
}