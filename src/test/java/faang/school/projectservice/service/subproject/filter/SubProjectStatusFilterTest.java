package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubProjectStatusFilterTest {
    SubProjectStatusFilter statusFilter = new SubProjectStatusFilter();
    FilterSubProjectDto filters = FilterSubProjectDto.builder()
            .statusPattern("IN_PROGRESS, CREATED")
            .build();

    @Test
    public void testApplicable() {
        assertTrue(statusFilter.isApplicable(filters));

        filters.setStatusPattern(null);
        assertFalse(statusFilter.isApplicable(filters));
    }

    @Test
    public void testApply() {
        Project goodProject = Project.builder()
                .status(ProjectStatus.CREATED)
                .build();

        Project badProject = Project.builder()
                .status(ProjectStatus.CANCELLED)
                .build();
        Stream<Project> stream = List.of(goodProject, badProject).stream();

        List<Project> result = statusFilter.apply(stream, filters).toList();

        assertTrue(result.contains(goodProject));
        assertEquals(1, result.size());
    }
}
