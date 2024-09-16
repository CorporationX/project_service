package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectDtoValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectDtoValidator projectDtoValidator;
    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setOwnerId(5L);
        projectDto.setName("name");
        projectDto.setDescription("description");
    }

    @Test
    @DisplayName("Проверка валидации отрицательного id")
    public void testValidateNegativeId() {
        assertThrows(DataValidationException.class,
                () -> projectDtoValidator.validateId(-1L));
    }


    @Test
    @DisplayName("Проверка валидации проекта при создании без требуемого поля name")
    public void testValidateDtoCreateNullName() {
        projectDto.setName(null);
        assertThrows(DataValidationException.class,
                () -> projectDtoValidator.validateDtoCreate(projectDto));
    }

    @Test
    @DisplayName("Проверка валидации проекта при создании с пустым именем")
    public void testValidateDtoCreateBlankName() {
        projectDto.setName("");
        assertThrows(DataValidationException.class,
                () -> projectDtoValidator.validateDtoCreate(projectDto));
        projectDto.setName(" ");
        assertThrows(DataValidationException.class,
                () -> projectDtoValidator.validateDtoCreate(projectDto));
        projectDto.setName("                         ");
        assertThrows(DataValidationException.class,
                () -> projectDtoValidator.validateDtoCreate(projectDto));
    }

    @Test
    @DisplayName("Проверка валидации проекта при обновлении с пустым описанием без статуса")
    public void testValidateDtoUpdateNullDescriptionNullStatus() {
        projectDto.setDescription(null);
        projectDto.setStatus(null);
        assertThrows(DataValidationException.class,
                () -> projectDtoValidator.validateDtoUpdate(projectDto));
    }
}
