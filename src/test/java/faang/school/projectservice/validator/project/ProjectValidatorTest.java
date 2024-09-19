package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectDtoValidator;

    @Mock
    private ProjectRepository projectRepository;

    private static final long USER_ID = 1L;
    private static final long PROJECT_ID_1 = 1L;
    private static final long PROJECT_ID_2 = 2L;
    private static final String PROJECT_NAME = "name";
    private static final String PROJECT_DESCRIPTION = "description";

    @Nested
    class PositiveTests {

        @Nested
        @DisplayName("ProjectDto Validation Tests")
        class ValidateProjectDtoTests {

            @Test
            @DisplayName("Success when project has a name, description, and owner does not have a project with the same name")
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
        @DisplayName("Project Validation Tests")
        class ValidateProjectsTests {

            private Project activeProject1;
            private Project activeProject2;

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
            }

            @Test
            @DisplayName("Success when the project list is valid and contains no cancelled projects")
            public void whenValidProjectListThenSuccess() {
                List<Project> projects = List.of(activeProject1, activeProject2);

                assertDoesNotThrow(() -> projectDtoValidator.validateProjects(projects));
            }
        }
    }

    @Nested
    class NegativeTests {

        @Nested
        @DisplayName("ProjectDto Validation Tests")
        class ValidateProjectDtoTests {
            @Test
            @DisplayName("Validation error when project name is empty")
            public void whenValidateWithEmptyNameThenException() {
                ProjectDto projectDto = new ProjectDto();
                projectDto.setName(" ");
                projectDto.setDescription(PROJECT_DESCRIPTION);

                assertThrows(DataValidationException.class,
                        () -> projectDtoValidator.validateProject(projectDto));
            }

            @Test
            @DisplayName("Validation error when project description is empty")
            public void whenValidateWithEmptyDescriptionThenException() {
                ProjectDto projectDto = new ProjectDto();
                projectDto.setName(PROJECT_NAME);
                projectDto.setDescription(" ");

                assertThrows(DataValidationException.class,
                        () -> projectDtoValidator.validateProject(projectDto));
            }

            @Test
            @DisplayName("Validation error when the owner has an existing project with the same name in the database")
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

        @Nested
        @DisplayName("Project Validation Tests")
        class ValidateProjectsTests {

            private Project activeProject;
            private Project cancelledProject;

            @BeforeEach
            void setUp() {
                activeProject = Project.builder()
                        .id(PROJECT_ID_1)
                        .status(ProjectStatus.CREATED)
                        .build();
                cancelledProject = Project.builder()
                        .id(PROJECT_ID_2)
                        .status(ProjectStatus.CANCELLED)
                        .build();
            }

            @Test
            @DisplayName("Error when the project list is empty")
            public void whenEmptyProjectListThenException() {
                List<Project> projects = List.of();

                DataValidationException exception = assertThrows(
                        DataValidationException.class,
                        () -> projectDtoValidator.validateProjects(projects)
                );

                assertEquals("The list of projects doesn't exist", exception.getMessage());
            }

            @Test
            @DisplayName("Success when the project list contains no cancelled projects")
            public void whenNoCancelledProjectsThenSuccess() {
                List<Project> projects = List.of(activeProject);

                assertDoesNotThrow(() -> projectDtoValidator.validateProjects(projects));
            }

            @Test
            @DisplayName("Error when the project list contains a cancelled project")
            public void whenCancelledProjectExistsThenException() {
                List<Project> projects = List.of(activeProject, cancelledProject);

                DataValidationException exception = assertThrows(
                        DataValidationException.class,
                        () -> projectDtoValidator.validateProjects(projects)
                );

                assertEquals("Cancelled project with ID 2", exception.getMessage());
            }
        }
    }
}