package faang.school.projectservice.validator.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    @Mock
    ProjectRepository projectRepository;
    @InjectMocks
    ProjectValidator projectValidator;

    @Test
    public void TestValidatorOpenProject() {
        Project project1 = new Project();
        Project project2 = new Project();
        project1.setStatus(ProjectStatus.CANCELLED);
        project1.setId(1L);
        project2.setStatus(ProjectStatus.CANCELLED);
        project1.setId(2L);
        List<Long> projectsIds = Arrays.asList(1L, 2L);
        List<Project> projects = Arrays.asList(project1, project2);
        Mockito.when(projectRepository.findAllByIds(projectsIds)).thenReturn(projects);

        assertThrows(IllegalArgumentException.class, () -> projectValidator.ValidatorOpenProject(projectsIds));
    }
}
