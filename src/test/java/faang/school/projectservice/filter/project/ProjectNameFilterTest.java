package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectNameFilterTest {
    private ProjectNameFilter projectNameFilter;
    private ProjectFilterDto filter;

    @BeforeEach
    void setUp() {
        projectNameFilter = new ProjectNameFilter();
        filter = ProjectFilterDto.builder()
                .name("test")
                .build();
    }

    @Test
    void testFilterIsNotApplicable() {
        filter.setName(null);
        assertFalse(projectNameFilter.isApplicable(filter));
    }

    @Test
    void testFilterIsApplicable() {
        assertTrue(projectNameFilter.isApplicable(filter));
    }

    @Test
    void testFilterApplySuccessful() {
        Stream<Project> projects = Stream.of(
                Project.builder().name("test").build(),
                Project.builder().name("another test").build()
        );

        List<Project> result = projectNameFilter.apply(projects, filter).toList();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getName(), filter.getName());
    }
}