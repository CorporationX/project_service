package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private SubProjectService subProjectService;
    private SubProjectService subProjectServiceMockFilter;

    private List<SubprojectFilter> subprojectFilters;
    private SubprojectFilterDto filter = new SubprojectFilterDto();
    List<Project> projects = new ArrayList<>();
    private long rightId;
    Project projectA = Project.builder()
            .status(ProjectStatus.IN_PROGRESS)
            .name("Project A")
            .build();
    Project projectB = Project.builder()
            .status(ProjectStatus.COMPLETED)
            .name("Project B")
            .build();
    Project projectC = Project.builder()
            .status(ProjectStatus.IN_PROGRESS)
            .name("Project C")
            .build();
    private Project parent = new Project();

    @BeforeEach
    public void setUp() {
        rightId = 1L;

        filter.setId(rightId);

        projects.add(projectA);
        projects.add(projectB);
        projects.add(projectC);

        parent.setChildren(projects);

        SubprojectFilter subProjectFilter = Mockito.mock(SubprojectFilter.class);

        subprojectFilters = List.of(subProjectFilter);

        subProjectService = new SubProjectService(projectService, new ArrayList<>(), projectMapper);
        subProjectServiceMockFilter = new SubProjectService(projectService, subprojectFilters, projectMapper);

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(parent);
    }

    @Test
    public void testGetSubProject() {
        SubprojectFilterDto filterProgress = SubprojectFilterDto.builder()
                .id(rightId)
                .nameFilter("Project A")
                .statusFilter(ProjectStatus.IN_PROGRESS)
                .build();

        Mockito.when(subprojectFilters.get(0).isApplicable(filterProgress))
                .thenReturn(true);
        Mockito.when(subprojectFilters.get(0).apply(any(), any()))
                .thenReturn(List.of(projectA).stream());

        subProjectService.getAllSubProject(filterProgress);
        Mockito.verify(projectService, Mockito.times(1))
                .getProjectById(rightId);

        assertEquals(1, subProjectServiceMockFilter.getAllSubProject(filterProgress).size());
        assertEquals(0, subProjectService.getAllSubProject(filterProgress).size());
    }
}