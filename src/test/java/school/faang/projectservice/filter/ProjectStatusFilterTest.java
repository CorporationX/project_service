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
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.model.ProjectStatus;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {
    private final ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();

    @Test
    public void testIsApplicable_whenNullStatus_thenReturnFalse() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        assertFalse(projectStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable_whenStatusExists_thenReturnTrue() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setStatus(ProjectStatus.COMPLETED);
        assertTrue(projectStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void testApply_whenStatusMatches_thenReturnFilteredList() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setStatus(ProjectStatus.IN_PROGRESS);

        ProjectDto projectDto1 = ProjectDto.builder().status(ProjectStatus.CANCELLED).build();
        ProjectDto projectDto2 = ProjectDto.builder().status(ProjectStatus.IN_PROGRESS).build();
        Stream<ProjectDto> projects = Stream.of(projectDto1, projectDto2);

        List<ProjectDto> result = projectStatusFilter.apply(projects, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(ProjectStatus.IN_PROGRESS, result.get(0).getStatus()); 
    }

    @Test
    public void testApply_whenNameDoesNotMatch_thenReturnEmptyList() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setStatus(ProjectStatus.IN_PROGRESS);

        ProjectDto projectDto1 = ProjectDto.builder().status(ProjectStatus.CANCELLED).build();
        ProjectDto projectDto2 = ProjectDto.builder().status(ProjectStatus.CREATED).build();
        Stream<ProjectDto> projects = Stream.of(projectDto1, projectDto2);

        List<ProjectDto> result = projectStatusFilter.apply(projects, filterDto).toList();

        assertTrue(result.isEmpty());
    }
}
