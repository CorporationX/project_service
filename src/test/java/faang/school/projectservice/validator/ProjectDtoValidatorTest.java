package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectDtoValidatorTest {

    @InjectMocks
    private ProjectDtoValidator projectDtoValidator;

    @Mock
    private ProjectRepository projectRepository;

    private static final long USER_ID = 1L;
    private static final String PROJECT_NAME = "name";
    private static final String PROJECT_DESCRIPTION = "description";

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех если проект имеет название, описание и владелец еще не имеет проекта с таким названием")
        public void whenValidateThenSuccess() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(PROJECT_NAME);
            projectDto.setDescription(PROJECT_DESCRIPTION);
            projectDto.setOwnerId(USER_ID);
            when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                    .thenReturn(false);

            projectDtoValidator.validateProject(projectDto);

            verify(projectRepository).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если у проекта нет названия")
        public void whenValidateWithEmptyNameThenException() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(" ");
            projectDto.setDescription(PROJECT_DESCRIPTION);

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateProject(projectDto));
        }

        @Test
        @DisplayName("Ошибка валидации если у проекта нет описания")
        public void whenValidateWithEmptyDescriptionThenException() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(PROJECT_NAME);
            projectDto.setDescription(" ");

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateProject(projectDto));
        }

        @Test
        @DisplayName("Ошибка если владелец имеет проект с таким названием в БД")
        public void whenValidateWithOwnerHasSameProjectThenException() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(PROJECT_NAME);
            projectDto.setDescription(PROJECT_DESCRIPTION);
            projectDto.setOwnerId(USER_ID);
            when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                    .thenReturn(true);

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateProject(projectDto));
        }
    }
}