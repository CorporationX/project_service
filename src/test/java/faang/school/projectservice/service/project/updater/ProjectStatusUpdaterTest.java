package faang.school.projectservice.service.project.updater;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusUpdaterTest {
    @InjectMocks
    private ProjectStatusUpdater projectStatusUpdater;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder().status(ProjectStatus.CREATED).build();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(projectStatusUpdater.isApplicable(projectDto));
        projectDto.setStatus(null);
        Assertions.assertFalse(projectStatusUpdater.isApplicable(projectDto));
    }

    @Test
    public void testApply() {
        Project projectResult = new Project();
        Project projectExpected = Project.builder().status(projectDto.getStatus()).build();

        projectStatusUpdater.apply(projectResult, projectDto);

        Assertions.assertEquals(projectExpected, projectResult);
    }
}
