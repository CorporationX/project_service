package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectStatusFilterTest {

    ProjectStatusFilter projectStatusFilter;

    @BeforeEach
    public void setUp() {
        projectStatusFilter = new ProjectStatusFilter();
    }

    @Test
    public void testIsApplicable() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().status(ProjectStatus.IN_PROGRESS).build();
        assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void testApply() {
        List<Project> projects = List.of(
                Project.builder().status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().status(ProjectStatus.CREATED).build(),
                Project.builder().status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().status(ProjectStatus.ON_HOLD).build());

        Stream<Project> expectedProjects = Stream.of(
                Project.builder().status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().status(ProjectStatus.IN_PROGRESS).build());

        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().status(ProjectStatus.IN_PROGRESS).build();

        assertEquals(expectedProjects.toList(), projectStatusFilter.apply(projects, projectFilterDto).toList());
    }
}
