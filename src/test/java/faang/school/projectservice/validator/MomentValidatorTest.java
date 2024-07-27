package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentValidatorTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private MomentValidator momentValidator;

    private Moment moment;
    private Project project;
    List<Project> projects;

    @BeforeEach
    void setUp() {
        momentValidator = new MomentValidator(projectRepository);
        moment = new Moment();
        moment.setName("test");
        project = new Project();
        project.setName("project");
        projects = new ArrayList<>();
    }

    @Test
    public void validateIfNameIsBlankTest() {
        moment.setName("");
        Assert.assertThrows(DataValidationException.class, () -> {
            momentValidator.validateMoment(moment);
        });
    }

    @Test
    public void validateIfNameIsNullTest() {
        moment.setName(null);
        Assert.assertThrows(NullPointerException.class, () -> {
            momentValidator.validateMoment(moment);
        });
    }

    @Test
    public void validateIfNotAssignedToAnyProjectTest() {
        moment.setProjects(Collections.emptyList());
        Assert.assertThrows(DataValidationException.class, () -> {
            momentValidator.validateMoment(moment);
        });
    }

    @Test
    public void validateIfAssignedToAnyProjectTest() {
        Mockito.when(projectRepository.existsById(Mockito.anyLong())).thenReturn(true);
        project.setName("test");
        project.setId(3L);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        moment.setProjects(Collections.singletonList(project));
        Assertions.assertDoesNotThrow(() -> momentValidator.validateMoment(moment));
    }

    @Test
    public void validateIfProjectsAreCancelledTest() {
        project.setStatus(ProjectStatus.CANCELLED);
        moment.setProjects(Collections.singletonList(project));
        Assert.assertThrows(DataValidationException.class, () -> {
            momentValidator.validateMoment(moment);
        });
    }

    @Test
    public void validateIfProjectsArCompletedTest() {
        project.setStatus(ProjectStatus.COMPLETED);
        moment.setProjects(Collections.singletonList(project));
        Assertions.assertThrows(DataValidationException.class, () -> momentValidator.validateMoment(moment));
    }

    @Test
    public void validateIfProjectsAreNotClosedTest() {
        Mockito.when(projectRepository.existsById(Mockito.anyLong())).thenReturn(true);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setId(3L);
        projects.add(project);
        Project newProject = Project.builder().name("newProject").id(2L).status(ProjectStatus.CREATED).build();
        projects.add(newProject);
        moment.setProjects(projects);
        Assertions.assertDoesNotThrow(() -> momentValidator.validateMoment(moment));
    }

}
