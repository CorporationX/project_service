package faang.school.projectservice.validator;

import faang.school.projectservice.exception.BlankFieldException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ResourceValidator {
    public void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new BlankFieldException("OriginalFilename is should be present");
        }

        if (contentType == null || file.getOriginalFilename().isBlank()) {
            throw new BlankFieldException("ContentType is should be present; file={}", filename);
        }

        if (file.isEmpty()) {
            throw new BlankFieldException("File content is empty; file={}", filename);
        }
    }
}
