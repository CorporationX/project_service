package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubProjectValidationTest {

    private final SubProjectValidation subProjectValidation = new SubProjectValidation();
    UpdateSubProjectDto updateSubProjectDto;
    Project project;
    long userId = 1L;
    long projectId = 1;

    @BeforeEach
    public void setUp() {
        updateSubProjectDto = new UpdateSubProjectDto(projectId,
                ProjectStatus.COMPLETED, ProjectVisibility.PUBLIC);
        project = Project.builder()
                .id(projectId)
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void updateSubProject_throwsExceptionIfUserIsNotOwner() {
        assertThrows(IllegalArgumentException.class, () -> subProjectValidation.updateSubProject(userId, updateSubProjectDto, project));
    }

    @Test
    void updateSubProject_throwsExceptionIfNotAllSubprojectsCompletedOrCancelled() {
        Project subProject1 = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Project subProject2 = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        List<Project> children = new ArrayList<>();
        children.add(subProject1);
        children.add(subProject2);
        project.setChildren(children);

        assertThrows(IllegalArgumentException.class, () ->
                subProjectValidation.updateSubProject(2L, updateSubProjectDto, project));
    }

    @Test
    void updateSubProject_doesNotThrowExceptionIfSubprojectsCompletedOrCancelled() {
        Project subProject1 = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        Project subProject2 = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        List<Project> children = new ArrayList<>();
        children.add(subProject1);
        children.add(subProject2);
        project.setChildren(children);

        assertDoesNotThrow(() -> subProjectValidation.updateSubProject(2L, updateSubProjectDto, project));
    }
}