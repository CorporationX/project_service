package faang.school.projectservice;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.excepcion.DataValidationException;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectValidator;

    @Test
    void validate_WhenProjectIsValid_ShouldNotThrowException() {
        ProjectDto validProject = ProjectDto.builder()
                .name("Valid Project")
                .description("Valid Description")
                .build();

        assertDoesNotThrow(() -> projectValidator.validate(validProject),
                "Не должно быть исключения для валидного проекта");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_WhenProjectNameIsNullOrEmpty_ShouldThrowException(String invalidName) {
        ProjectDto invalidProject = ProjectDto.builder()
                .name(invalidName)
                .description("Valid Description")
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validate(invalidProject),
                "Должно быть выброшено DataValidationException при пустом имени");

        assertEquals("Project name cannot be empty", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_WhenProjectDescriptionIsNullOrEmpty_ShouldThrowException(String invalidDescription) {
        ProjectDto invalidProject = ProjectDto.builder()
                .name("Valid Name")
                .description(invalidDescription)
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validate(invalidProject),
                "Должно быть выброшено DataValidationException при пустом описании");

        assertEquals("Project description cannot be empty", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
    }

    @Test
    void validate_WhenEntireProjectIsNull_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> projectValidator.validate(null),
                "Должно быть выброшено NullPointerException при null проекте");
    }
}
