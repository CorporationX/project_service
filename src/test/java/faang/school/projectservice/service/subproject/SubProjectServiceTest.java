package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private List<SubprojectFilter> subprojectFilter;
    @InjectMocks
    private SubProjectService subProjectService;

    @Test
    public void testGetAllSubProject() {
        long rightId = 1L;

        SubprojectFilterDto filter = new SubprojectFilterDto();
        filter.setId(rightId);

        List<Project> projects = new ArrayList<>();

        Project project = new Project();
        project.setChildren(new ArrayList<>());

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(project);
        Mockito.when(projectMapper.toListProjectDto(projects))
                .thenReturn(new ArrayList<>());

        subProjectService.getAllSubProject(filter);
        Mockito.verify(projectService, Mockito.times(1)).getProjectById(rightId);
        Mockito.verify(subprojectFilter, Mockito.times(1)).stream();
        Mockito.verify(projectMapper, Mockito.times(1)).toListProjectDto(projects);
    }
}