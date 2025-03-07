package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EmptyFileException;
import faang.school.projectservice.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RequiredArgsConstructor
public class FileValidator {
    private final long maxFileSize;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("File is empty or null");
        }

        if (!Objects.requireNonNull(file.getContentType()).equalsIgnoreCase("image/jpeg")) {
            throw new InvalidFileFormatException("Only JPEG images are allowed");
        }

        if (file.getSize() > maxFileSize) {
            throw new MaxUploadSizeExceededException(maxFileSize);
        }
    }
}
