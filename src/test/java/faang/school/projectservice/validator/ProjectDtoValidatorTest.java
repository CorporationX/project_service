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
import static org.mockito.Mockito.times;
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
        @DisplayName("Успех если владелец не имеет проекта с таким названием в БД")
        public void testWhenValidateIfOwnerAlreadyExistProjectWithNameReturnFalse() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(PROJECT_NAME);
            projectDto.setOwnerId(USER_ID);
            when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                    .thenReturn(false);

            projectDtoValidator.validateIfOwnerAlreadyExistProjectWithName(projectDto);

            verify(projectRepository).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
        }

        @Test
        @DisplayName("Успех если проект существует в БД")
        public void testWhenValidateIfProjectIsExistInDbReturnTrue() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(USER_ID);
            when(projectRepository.existsById(projectDto.getId())).thenReturn(true);

            projectDtoValidator.validateIfProjectIsExistInDb(projectDto.getId());

            verify(projectRepository).existsById(projectDto.getId());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если у проекта нет названия")
        public void testValidateIfProjectNameAndDescriptionWithNameIsEmpty() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(" ");
            projectDto.setDescription(PROJECT_DESCRIPTION);

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateIfProjectNameOrDescriptionIsBlank(projectDto));
        }

        @Test
        @DisplayName("Ошибка валидации если у проекта нет описания")
        public void testValidateIfProjectNameAndDescriptionWithDescriptionIsEmpty() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(PROJECT_NAME);
            projectDto.setDescription(" ");

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateIfProjectNameOrDescriptionIsBlank(projectDto));
        }

        @Test
        @DisplayName("Ошибка если владелец имеет проект с таким названием в БД")
        public void testWhenValidateIfOwnerAlreadyExistProjectWithNameReturnTrue() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(PROJECT_NAME);
            projectDto.setOwnerId(USER_ID);
            when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                    .thenReturn(true);

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateIfOwnerAlreadyExistProjectWithName(projectDto));
        }

        @Test
        @DisplayName("Ошибка если проект не существует в БД")
        public void testWhenValidateIfProjectIsExistInDbReturnFalse() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(USER_ID);
            when(projectRepository.existsById(projectDto.getId())).thenReturn(false);

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateIfProjectIsExistInDb(projectDto.getId()));
        }

        @Test
        @DisplayName("Ошибка если проект не содержит имеющийся статус")
        public void testValidateIfDtoContainsExistedProjectStatusWithInvalidProjectStatus() {
            ProjectDto projectDto = new ProjectDto();

            assertThrows(DataValidationException.class,
                    () -> projectDtoValidator.validateIfDtoContainsExistedProjectStatus(projectDto.getStatus()));
        }
    }
}