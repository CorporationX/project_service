package faang.school.projectservice.service.project.updater;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectDescriptionUpdaterTest {
    @InjectMocks
    private ProjectDescriptionUpdater projectDescriptionUpdater;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder().description("description").build();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(projectDescriptionUpdater.isApplicable(projectDto));
        projectDto.setDescription(null);
        Assertions.assertFalse(projectDescriptionUpdater.isApplicable(projectDto));
    }

    @Test
    public void testApply() {
        Project projectResult = new Project();
        Project projectExpected = Project.builder().description(projectDto.getDescription()).build();

        projectDescriptionUpdater.apply(projectResult, projectDto);

        Assertions.assertEquals(projectExpected, projectResult);
    }
}
