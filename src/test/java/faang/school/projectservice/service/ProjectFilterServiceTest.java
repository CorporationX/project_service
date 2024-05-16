package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectFilterServiceTest {
    @InjectMocks
    private ProjectFilterService projectFilterService;
    @Mock
    private List<ProjectFilter> projectFilters = List.of(new ProjectStatusFilter(), new ProjectNameFilter());

    private Project project;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    void init() {
        projectFilterDto = ProjectFilterDto.builder().name("Name").build();
        project = Project.builder().name("Name").build();
    }

    @Test
    public void testApplyFiltersWithNullProjectFilterDto() {
       Stream<Project> result = projectFilterService.applyFilters(Stream.of(project), null);
       assertEquals(result.toList().get(0), project);
    }

    @Test
    public void testApplyFiltersWithApplying(){
        Stream<Project> result = projectFilterService.applyFilters(Stream.of(project), projectFilterDto);
        assertEquals(result.toList().get(0), project);
    }
}
