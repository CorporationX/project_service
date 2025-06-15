package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectVisibilityFilterTest {

    private final ProjectVisibilityFilter visibilityFilter = new ProjectVisibilityFilter();

    @Test
    void testIsApplicableFalse() {
        boolean result = visibilityFilter.isApplicable(ProjectFilterDto.builder().build());
        assertFalse(result);
    }

    @Test
    void testIsApplicableTrue() {
        boolean result = visibilityFilter.isApplicable(ProjectFilterDto.builder().visibility(ProjectVisibility.PUBLIC).build());
        assertTrue(result);
    }

    @Test
    void testApplyMatchesExist() {
        ProjectFilterDto dto = ProjectFilterDto.builder().visibility(ProjectVisibility.PUBLIC).build();
        Stream<Project> projects = Stream.of(
                Project.builder().id(1L).visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().id(2L).visibility(ProjectVisibility.PRIVATE).build(),
                Project.builder().id(3L).visibility(ProjectVisibility.PRIVATE).build()
        );
        List<Project> projectList = visibilityFilter.apply(projects, dto).toList();

        assertEquals(1, projectList.size());
        assertEquals(dto.getVisibility(), projectList.get(0).getVisibility());
    }

    @Test
    void testApplyNoMatches(){
        ProjectFilterDto dto = ProjectFilterDto.builder().visibility(ProjectVisibility.PRIVATE).build();
        Stream<Project> projects = Stream.of(
                Project.builder().id(1L).visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().id(2L).visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().id(3L).visibility(ProjectVisibility.PUBLIC).build()
        );
        List<Project> projectList = visibilityFilter.apply(projects, dto).toList();

        assertEquals(0, projectList.size());
    }
}