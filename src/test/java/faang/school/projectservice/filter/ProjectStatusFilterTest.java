package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectStatusFilterTest {
    private ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
    private Project project;
    private Project project2;
    private Stream<Project> projectStream;
    private Stream<Project> projectStream2;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    void setUp() {
        projectFilterDto = ProjectFilterDto.builder().status(ProjectStatus.CREATED).build();
        project = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        project2 = Project.builder().status(ProjectStatus.CREATED).build();
        projectStream = Stream.of(project, project2);
        projectStream2 = Stream.of(project2);
    }

    @Test
    void testIsApplicable() {
        assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyFilter() {
        assertArrayEquals(projectStream2.toArray(), projectStatusFilter.applyFilter(projectFilterDto, projectStream).toArray());
    }
}