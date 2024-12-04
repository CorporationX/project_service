package faang.school.projectservice.validator;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    private ProjectValidator projectValidator;

    private CreateSubProjectDto createSubProjectDto;

    @BeforeEach
    public void setUp() {
        createSubProjectDto = CreateSubProjectDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectValidator = new ProjectValidator();
    }

    @Test
    public void testValidateCreateSubProject() {
        // arrange
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        // act and assert
        assertDoesNotThrow(() ->
                projectValidator.validateCreateSubProject(parentProject, createSubProjectDto));
    }

    @Test
    public void testValidateCreateSubProjectInactiveParentProject() {
        // arrange
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CANCELLED)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateCreateSubProject(parentProject, createSubProjectDto));
    }

    @Test
    public void testValidateCreateSubProjectVisibilityDoesNotMatch() {
        // arrange
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateCreateSubProject(parentProject, createSubProjectDto));
    }

    @Test
    public void testValidateUpdateSubProject() {
        // arrange
        Project firstSubProject = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        Project secondSubProject = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        List<Project> children = List.of(firstSubProject, secondSubProject);

        Project project = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .children(children)
                .build();

        UpdateSubProjectDto updateSubProjectDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        // act and assert
        assertDoesNotThrow(() ->
                projectValidator.validateUpdateSubProject(project, updateSubProjectDto));
    }

    @Test
    public void testValidateUpdateSubProjectNoMatchingStatus() {
        // arrange
        Project firstSubProject = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        Project secondSubProject = Project.builder()
                .status(ProjectStatus.CREATED)
                .build();
        List<Project> children = List.of(firstSubProject, secondSubProject);

        Project project = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .children(children)
                .build();

        UpdateSubProjectDto updateSubProjectDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateUpdateSubProject(project, updateSubProjectDto));
    }

    @Test
    public void testValidateUpdateSubProjectVisibilityDoesNotMatchParentChild() {
        // arrange
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project project = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(parentProject)
                .build();
        parentProject.setChildren(List.of(project));

        UpdateSubProjectDto updateSubProjectDto = UpdateSubProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateUpdateSubProject(project, updateSubProjectDto));
    }

    @Test
    public void testValidateUpdateSubProjectStatusDoesNotMatchParentChild() {
        // arrange
        Project parentProject = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project project = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .parentProject(parentProject)
                .build();
        parentProject.setChildren(List.of(project));
        UpdateSubProjectDto updateSubProjectDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateUpdateSubProject(project, updateSubProjectDto));
    }
}


