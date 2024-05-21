package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectFilterServiceTest {
    private ProjectFilterService projectFilterService;

    private Project firstProject;
    private ProjectFilterDto projectFilterDto;
    private ProjectNameFilter nameFilter;

    @BeforeEach
    void init() {
        nameFilter = Mockito.mock(ProjectNameFilter.class);
        List<ProjectFilter> filters = List.of(nameFilter);
        projectFilterService = new ProjectFilterService(filters);

        projectFilterDto = ProjectFilterDto.builder().name("Name").build();
        firstProject = Project.builder().name("Name").build();
    }

    @Test
    public void testApplyFiltersWithNullProjectFilterDto() {
        var exception = assertThrows(NullPointerException.class, ()->projectFilterService.applyFilters(Stream.of(firstProject), null));
        assertEquals(exception.getMessage(), "projectFilterDto is marked non-null but is null");
    }

    @Test
    public void testApplyFiltersWithApplying() {
        Stream<Project> projectStream = Stream.of(firstProject);
        when(nameFilter.isApplicable(projectFilterDto)).thenReturn(true);
        when(nameFilter.apply(projectStream, projectFilterDto)).thenReturn(projectStream);
        List<Project> result = projectFilterService.applyFilters(projectStream, projectFilterDto).toList();
        verify(nameFilter, times(1)).isApplicable(projectFilterDto);
        verify(nameFilter, times(1)).apply(projectStream, projectFilterDto);
        assertEquals(1, result.size());
        assertEquals(result.get(0), firstProject);
    }
}
