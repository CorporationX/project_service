package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectStatus.ON_HOLD;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SubProjectValidatorTest {

    private SubProjectValidator validator;
    private Project parent;
    private CreateSubProjectDto createSubProjectDto;
    private ProjectDto projectDto;
    private List<Project> projects;

    @BeforeEach
    void setUp() {
        validator = new SubProjectValidator();
        parent = Project.builder().visibility(PRIVATE).build();
        createSubProjectDto = CreateSubProjectDto.builder().visibility(PUBLIC).build();
        projectDto = ProjectDto.builder().visibility(PUBLIC).build();
        projects = new ArrayList<>();
        projects.add(Project.builder().status(COMPLETED).build());
        projects.add(Project.builder().status(COMPLETED).build());
    }

    @Test
    void testValidateSubProjectVisibilityNotValidVisibility() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validateSubProjectVisibility(parent, createSubProjectDto));
    }

    @Test
    void testIsAllSubProjectsCompleted() {
        parent.setChildren(projects);
        assertTrue(validator.isAllSubProjectsCompleted(parent));
    }

    @Test
    void testIsNotAllSubProjectsCompleted() {
        projects.add(Project.builder().status(ON_HOLD).build());
        parent.setChildren(projects);
        assertFalse(validator.isAllSubProjectsCompleted(parent));
    }

    @Test
    void validCorrectVisibility() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validCorrectVisibility(projectDto, parent));
    }
}
