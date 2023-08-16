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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    @Mock
    private ProjectService projectService;
    @Spy
    private ProjectMapper projectMapper;
    @Mock
    private List<SubprojectFilter> subprojectFilter;
    @InjectMocks
    private SubProjectService subProjectService;
    private long rightId;
    SubprojectFilterDto filter;
    List<Project> projects = new ArrayList<>();

    Project projectA = Project.builder()
            .name("Project A")
            .status(ProjectStatus.IN_PROGRESS)
            .build();
    Project projectB = Project.builder()
            .name("Project B")
            .status(ProjectStatus.COMPLETED)
            .build();
    Project projectC = Project.builder()
            .name("Project C")
            .status(ProjectStatus.IN_PROGRESS)
            .build();

    Project parentProject = Project.builder()
            .children(List.of(projectA, projectB, projectC))
            .build();

    @BeforeEach
    public void setUp() {
        rightId = 1L;

        filter = new SubprojectFilterDto();
        filter.setId(rightId);

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(parentProject);

    }

    @Test
    public void testGetAllSubProject() {
        subProjectService.getAllSubProject(filter);
        Mockito.verify(projectService, Mockito.times(1)).getProjectById(rightId);
        Mockito.verify(subprojectFilter, Mockito.times(1)).stream();
        Mockito.verify(projectMapper, Mockito.times(1)).toListProjectDto(parentProject.getChildren());
    }

    @Test

    public void testGetAllSubProjectFilterName() {
        filter = SubprojectFilterDto.builder()
                .id(rightId)
                .nameFilter("Project A")
                .build();
        assertEquals(2, subProjectService.getAllSubProject(filter).size());

    }

    @Test
    public void testGetAllSubProjectFilterStatus() {

    }

    @Test
    public void testGetAllSubProjectFilter() {

    }
}