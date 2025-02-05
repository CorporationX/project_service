package faang.school.projectservice.validator.project;

import faang.school.projectservice.config.amazon.ResourceConfig;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileManagementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileValidator {
    private final ResourceConfig resourceConfig;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DataValidationException("Загрузка невозможна: файл пустой");
        }
    }

    public void checkFileSize(long fileSize) {
        if (fileSize > resourceConfig.getMaxSize()) {
            throw new FileManagementException("Размер файла не должен превышать 5 МБ");
        }
    }
}
