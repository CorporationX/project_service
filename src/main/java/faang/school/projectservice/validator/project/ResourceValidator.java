package faang.school.projectservice.validator.project;

import faang.school.projectservice.config.resource.ResourceConfig;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileManagementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ResourceValidator {
    private final ResourceConfig resourceConfig;

    public void validateResource(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DataValidationException("Загрузка невозможна: файл пустой");
        }
    }

    public void checkFileSize(long fileSize) {
        if (fileSize > resourceConfig.getMaxSize()) {
            throw new FileManagementException("Размер файла не должен превышать 5 МБ");
        }
    }

    public void checkIsFileImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (!isImage(contentType)) {
            throw new DataValidationException("Загрузка невозможна: файл не является изображением");
        }

        String originalFilename = file.getOriginalFilename();
        if (!isValidImageExtension(originalFilename)) {
            throw new DataValidationException("Загрузка невозможна: недопустимое расширение файла");
        }
    }

    private boolean isImage(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/");
    }

    private boolean isValidImageExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return false;
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        return extension.endsWith(".jpg")
                || extension.endsWith(".jpeg")
                || extension.endsWith(".png")
                || extension.endsWith(".gif");
    }
}
