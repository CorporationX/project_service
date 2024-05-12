package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectNameFilterTest {

    ProjectNameFilter projectNameFilter;

    @BeforeEach
    public void setUp() {
        projectNameFilter = new ProjectNameFilter();
    }

    @Test
    public void testIsApplicable() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().name("Project1").build();
        assertTrue(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void testApply() {
        List<Project> projects = List.of(
                Project.builder().name("FirstProject").build(),
                Project.builder().name("SecondProject").build(),
                Project.builder().name("ThirdProject").build(),
                Project.builder().name("ThirdProject").build());

        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().name("ThirdProject").build();

        Stream<Project> expectedProjects = Stream.of(
                Project.builder().name("ThirdProject").build(),
                Project.builder().name("ThirdProject").build());

        assertEquals(expectedProjects.toList(), projectNameFilter.apply(projects, projectFilterDto).toList());
    }
}