package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    private ProjectValidator projectValidator;

    @Mock
    List<Resource> resources;

    @BeforeEach
    void setUp() {
        projectValidator = new ProjectValidator();
    }

    @Test
    void validateProjectStorageSizeWhenNewStorageSizeIsGreaterThanMaxStorage() {
        Project project = Project.builder().maxStorageSize(BigInteger.TWO).build();
        BigInteger newStorageSize = BigInteger.TEN;
        BigInteger fileSize = BigInteger.ONE;

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectStorageSize(newStorageSize, project, fileSize),
                String.format("Загрузка невозможна: максимальный размер хранилища %d, размер файла %d",
                        project.getMaxStorageSize(), fileSize)
        );
    }

    @Test
    void validateProjectStorageSizeWhenReachedMaxCountFiles() {
        when(resources.size()).thenReturn(50);
        Project project = Project.builder()
                .maxStorageSize(BigInteger.TWO)
                .resources(resources)
                .build();
        BigInteger newStorageSize = BigInteger.ONE;
        BigInteger fileSize = BigInteger.ONE;

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectStorageSize(newStorageSize, project, fileSize),
                "Файл не может быть добавлен в хранилище, так как превышен максимальный лимит в 50 файлов"
        );
    }
}