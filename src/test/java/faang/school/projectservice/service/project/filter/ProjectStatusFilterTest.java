package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {
    @InjectMocks
    private ProjectStatusFilter projectStatusFilter;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    void setUp() {
        projectFilterDto = ProjectFilterDto.builder().name("Name").status(ProjectStatus.CREATED).build();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
        projectFilterDto.setStatus(null);
        Assertions.assertFalse(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void testApply() {
        Project project1 = Project.builder().status(ProjectStatus.CREATED).build();
        Project project2 = Project.builder().status(ProjectStatus.IN_PROGRESS).build();

        Stream<Project> stream = Stream.of(project1, project2);
        Stream<Project> expectedStream = Stream.of(project1);
        Stream<Project> resultStream = projectStatusFilter.apply(stream, projectFilterDto);

        Assertions.assertEquals(expectedStream.toList(), resultStream.toList());
    }
}
