package faang.school.projectservice.validator.project;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    private static final long USER_ID = 1L;
    private static final String PROJECT_NAME = "name";
    private static final String PROJECT_DESCRIPTION = "description";

    @Nested
    class PositiveTests {

        @Nested
        class NegativeTests {

            @Test
            @DisplayName("Ошибка если владелец имеет проект с таким названием в БД")
            public void whenValidateWithOwnerHasSameProjectThenException() {
                ProjectDto projectDto = new ProjectDto();
                projectDto.setName(PROJECT_NAME);
                projectDto.setDescription(PROJECT_DESCRIPTION);
                projectDto.setOwnerId(USER_ID);
                when(projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                        .thenReturn(true);

                assertThrows(DataValidationException.class,
                        () -> projectValidator.validateOwnerHasSameProject(projectDto));
            }
        }
    }
}