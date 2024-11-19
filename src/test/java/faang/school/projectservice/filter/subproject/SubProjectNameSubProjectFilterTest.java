package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubProjectNameSubProjectFilterTest {
    private final SubProjectNameSubProjectFilter subProjectNameFilter = new SubProjectNameSubProjectFilter();

    @Test
    void testIsApplicableNotNullNotEmptyTrue() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().name("project").build();
        boolean result = subProjectNameFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    void testIsApplicableWithEmptyFalse() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().name("").build();
        boolean result = subProjectNameFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testIsApplicableWithNullFalse() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().build();
        boolean result = subProjectNameFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplySuccess() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().name("project").build();
        List<Project> projectStream = List.of(
                Project.builder().name("First").build(),
                Project.builder().name("Second project").build()
        );
        List<Project> projectsFiltered = List.of(Project.builder().name("Second project").build());

        Stream<Project> result = subProjectNameFilter.apply(projectStream.stream(), filterDto);
        assertEquals(result.toList(), projectsFiltered);
    }

    @Test
    void testApplyReturnFalseNullName() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().build();

        boolean result = subProjectNameFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplyReturnEmptyProjectNotMatch() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().name("Second project").build();
        List<Project> projectStream = List.of(
                Project.builder().name("First").build()
        );

        Stream<Project> result = subProjectNameFilter.apply(projectStream.stream(), filterDto);
        assertTrue(result.toList().isEmpty());
    }
}