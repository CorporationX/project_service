package school.faang.projectservice.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectNameFilter;

@ExtendWith(MockitoExtension.class)
public class ProjectNameFilterTest {
    private final ProjectNameFilter projectNameFilter = new ProjectNameFilter();

    @Test
    public void testIsApplicable_whenEmptyName_thenReturnFalse() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setName("");
        assertFalse(projectNameFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable_whenNullName_thenReturnFalse() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        assertFalse(projectNameFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable_whenNameExists_thenReturnTrue() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setName("name filter");
        assertTrue(projectNameFilter.isApplicable(filterDto));
    }

    @Test
    public void testApply_whenNameMatches_thenReturnFilteredList() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setName("Test");

        ProjectDto projectDto1 = ProjectDto.builder().name("Test x").build();
        ProjectDto projectDto2 = ProjectDto.builder().name("Test").build();
        Stream<ProjectDto> projects = Stream.of(projectDto1, projectDto2);

        List<ProjectDto> result = projectNameFilter.apply(projects, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName()); 
    }

    @Test
    public void testApply_whenNameDoesNotMatch_thenReturnEmptyList() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setName("none");

        ProjectDto projectDto1 = ProjectDto.builder().name("Test x").build();
        ProjectDto projectDto2 = ProjectDto.builder().name("Test").build();
        Stream<ProjectDto> projects = Stream.of(projectDto1, projectDto2);

        List<ProjectDto> result = projectNameFilter.apply(projects, filterDto).toList();

        assertTrue(result.isEmpty());
    }
}
