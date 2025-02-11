package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class ProjectValidator {
    private static final long FILE_MAX_COUNT_IN_PROJECT_GALLERY = 50;

    public void validateProjectStorageSize(BigInteger newStorageSize, Project project, BigInteger fileSize) {
        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new DataValidationException(String.format(
                    "Загрузка невозможна: максимальный размер хранилища %d, размер файла %d",
                    project.getMaxStorageSize(), fileSize));
        }

        if (project.getResources().size() >= FILE_MAX_COUNT_IN_PROJECT_GALLERY) {
            throw new DataValidationException(
                    String.format("Файл не может быть добавлен в хранилище, " +
                            "так как превышен максимальный лимит в %d файлов", FILE_MAX_COUNT_IN_PROJECT_GALLERY));
        }
    }
}
