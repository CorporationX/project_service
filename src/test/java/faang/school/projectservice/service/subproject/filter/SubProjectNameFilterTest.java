package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubProjectNameFilterTest {
    SubProjectNameFilter nameFilter = new SubProjectNameFilter();
    FilterSubProjectDto filters = FilterSubProjectDto.builder()
            .namePattern("Minotaur")
            .build();

    @Test
    public void testApplicable() {
        assertTrue(nameFilter.isApplicable(filters));

        filters.setNamePattern(null);
        assertFalse(nameFilter.isApplicable(filters));
    }

    @Test
    public void testApply() {
        Project goodProject = Project.builder()
                .name("Project of team Minotaur")
                .build();

        Project badProject = Project.builder()
                .name("Project of team Manticora")
                .build();
        Stream<Project> stream = List.of(goodProject, badProject).stream();

        List<Project> result = nameFilter.apply(stream, filters).toList();

        assertTrue(result.contains(goodProject));
        assertEquals(1, result.size());
    }
}
