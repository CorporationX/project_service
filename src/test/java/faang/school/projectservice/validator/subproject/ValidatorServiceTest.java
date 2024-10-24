package faang.school.projectservice.validator.subproject;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.enums.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@Component
@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ValidatorService validatorService;

    @Test
    void testIsProjectExistsTrue() {
        Long projectId = 10L;
        when(projectRepository.existsById(projectId)).thenReturn(true);
        assertDoesNotThrow(() -> validatorService.isProjectExists(projectId));
    }

    @Test
    void testIsProjectExistsFalse() {
        Long projectId = 10L;
        when(projectRepository.existsById(projectId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> validatorService.isProjectExists(projectId));

        assertEquals("Project with id " + projectId + " does not exist", exception.getErrorMessage());
    }

    @Test
    void testIsProjectExistsByNameFalse() {
        String projectName = "Second Pr";
        when(projectRepository.findAll()).thenReturn(createProjects());

        assertDoesNotThrow(() -> validatorService.isProjectExistsByName(projectName));
    }

    @Test
    void testIsProjectExistsByNameTrue() {
        String projectName = "First Project";
        when(projectRepository.findAll()).thenReturn(createProjects());

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> validatorService.isProjectExistsByName(projectName));

        assertEquals("Project with name " + projectName + " already exists", exception.getErrorMessage());
    }

    @Test
    void isVisibilityRightTrue() {
        ProjectVisibility parentProjectVisibility = ProjectVisibility.PRIVATE;
        ProjectVisibility subProjectVisibility = ProjectVisibility.PRIVATE;

        assertDoesNotThrow(() -> validatorService.isVisibilityRight(parentProjectVisibility, subProjectVisibility));
    }

    @Test
    void isVisibilityRightFalse() {
        ProjectVisibility parentProjectVisibility = ProjectVisibility.PUBLIC;
        ProjectVisibility subProjectVisibility = ProjectVisibility.PRIVATE;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validatorService.isVisibilityRight(parentProjectVisibility, subProjectVisibility));
        assertEquals("Can't create SubProject, because ParentProject visibility is public", exception.getMessage());
    }

    public static List<Project> createProjects() {
        Project firstProject = new Project();
        firstProject.setName("First Project");
        Project secondProject = new Project();
        secondProject.setName("Second Project");
        return List.of(firstProject, secondProject);
    }
}