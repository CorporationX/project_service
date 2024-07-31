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
public class ProjectNameFilterTest {
    @InjectMocks
    private ProjectNameFilter projectNameFilter;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    void setUp() {
        projectFilterDto = ProjectFilterDto.builder().name("Name").status(ProjectStatus.CREATED).build();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(projectNameFilter.isApplicable(projectFilterDto));
        projectFilterDto.setName(null);
        Assertions.assertFalse(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void testApply() {
        Project project1 = Project.builder().name("Name").build();
        Project project2 = Project.builder().name("Name2").build();

        Stream<Project> stream = Stream.of(project1, project2);
        Stream<Project> expectedStream = Stream.of(project1);
        Stream<Project> resultStream = projectNameFilter.apply(stream, projectFilterDto);

        Assertions.assertEquals(expectedStream.toList(), resultStream.toList());
    }
}
