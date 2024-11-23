package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectStatusFilterTest {
    private ProjectStatusFilter projectStatusFilter;
    private ProjectFilterDto filter;

    @BeforeEach
    void setUp() {
        projectStatusFilter = new ProjectStatusFilter();
        filter = ProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();
    }

    @Test
    void testFilterIsNotApplicable() {
        filter.setStatus(null);
        assertFalse(projectStatusFilter.isApplicable(filter));
    }

    @Test
    void testFilterIsApplicable() {
        assertTrue(projectStatusFilter.isApplicable(filter));
    }

    @Test
    void testFilterApplySuccessful() {
        Stream<Project> projects = Stream.of(
                Project.builder().status(ProjectStatus.CREATED).build(),
                Project.builder().status(ProjectStatus.CANCELLED).build()
        );

        List<Project> result = projectStatusFilter.apply(projects, filter).toList();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getStatus(), filter.getStatus());
    }
}