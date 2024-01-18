package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectNameFilterTest {
    private ProjectNameFilter projectNameFilter = new ProjectNameFilter();
    private Project project;
    private Project project2;
    private Stream<Project> projectStream;
    private Stream<Project> projectStream2;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    void setUp() {
        projectFilterDto = ProjectFilterDto.builder().name("Java").build();
        project = Project.builder().name("NotJava").build();
        project2 = Project.builder().name("Java").build();
        projectStream = Stream.of(project, project2);
        projectStream2 = Stream.of(project2);
    }

    @Test
    void testIsApplicable() {
        assertTrue(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyFilter() {
        assertArrayEquals(projectStream2.toArray(), projectNameFilter.applyFilter(projectFilterDto, projectStream).toArray());
    }
}