package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectVisibilityFilterTest {

    ProjectVisibilityFilter projectVisibilityFilter = new ProjectVisibilityFilter();
    ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    List<Project> projects;

    @BeforeEach
    void setUp() {
        Project project1 = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project project2 = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        projects = List.of(project1, project2);
    }

    @Test
    void testIsApplicableWhenVisibilityPatternExist() {
        projectFilterDto.setVisibilityPattern(ProjectVisibility.PUBLIC);
        assertTrue(projectVisibilityFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableWhenVisibilityPatternNotExist() {
        assertFalse(projectVisibilityFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyWhenVisibilityPatternExistShouldReturnNotEmptyStream() {
        projectFilterDto.setVisibilityPattern(ProjectVisibility.PUBLIC);
        Stream<Project> result = projectVisibilityFilter.apply(projects.stream(), projectFilterDto);
        assertEquals(projects, result.toList());
    }

    @Test
    void testApplyWhenVisibilityPatternExistAndNoMatchesShouldReturnEmptyStream() {
        projectFilterDto.setVisibilityPattern(ProjectVisibility.PRIVATE);
        List<Project> result = projectVisibilityFilter.apply(projects.stream(), projectFilterDto).toList();
        assertNotEquals(projects, result);
        assertEquals(0, result.size());
    }
}