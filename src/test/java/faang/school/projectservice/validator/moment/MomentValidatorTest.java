package faang.school.projectservice.validator.moment;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {

    @InjectMocks
    private MomentValidator momentValidator;

    @Mock
    private MomentRepository momentRepositoryRepository;

    private static final long PROJECT_ID_1 = 1L;
    private static final long PROJECT_ID_2 = 2L;
    private static final long PROJECT_ID_3 = 3L;

    @Nested
    @DisplayName("Project Validation Tests")
    class ValidateProjectsTests {

        private Project activeProject1;
        private Project activeProject2;
        private Project cancelledProject;

        @BeforeEach
        void setUp() {
            activeProject1 = Project.builder()
                    .id(PROJECT_ID_1)
                    .status(ProjectStatus.CREATED)
                    .build();
            activeProject2 = Project.builder()
                    .id(PROJECT_ID_2)
                    .status(ProjectStatus.CREATED)
                    .build();
            cancelledProject = Project.builder()
                    .id(PROJECT_ID_3)
                    .status(ProjectStatus.CANCELLED)
                    .build();
        }

        @Test
        @DisplayName("Success when the project list contains no cancelled projects")
        public void whenNoCancelledProjectsThenSuccess() {
            List<Project> projects = List.of(activeProject1, activeProject2);

            assertDoesNotThrow(() -> momentValidator.validateNoCancelledProjects(projects));
        }

        @Test
        @DisplayName("Error when the project list contains a cancelled project")
        public void whenCancelledProjectExistsThenException() {
            List<Project> projects = List.of(activeProject1, cancelledProject);

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> momentValidator.validateNoCancelledProjects(projects)
            );

            assertEquals("Found cancelled project with id 3", exception.getMessage());
        }
    }
}