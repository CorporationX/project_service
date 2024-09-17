package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project_service.ProjectServiceImpl;
import faang.school.projectservice.validator.ValidatorProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private ValidatorProject validation;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private List<ProjectFilters> filters;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    public void testUpdateStatusGetException() {
        ProjectDto projectDto = new ProjectDto();
        Project project = new Project();
        project.setStatus(ProjectStatus.COMPLETED);
        ProjectStatus status = ProjectStatus.CANCELLED;

        when(validation.getEntity(projectDto)).thenReturn(project);
        assertThrows(NoSuchElementException.class, () -> projectService.updateStatus(projectDto, status));
    }

    @Test
    public void testUpdateStatus() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        ProjectStatus status = ProjectStatus.CANCELLED;
        when(validation.getEntity(projectDto)).thenReturn(projectEntity);
        when(projectRepository.existsById(projectEntity.getId())).thenReturn(true);

        assertDoesNotThrow(() -> projectService.updateStatus(projectDto, status));
    }

    @Test
    public void testUpdateDescriptionGetException() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        when(validation.getEntity(projectDto)).thenReturn(projectEntity);

        assertThrows(NoSuchElementException.class,
                () -> projectService.updateDescription(projectDto, "description"));
    }

    @Test
    public void testUpdateDescription() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setDescription("Start description");
        when(validation.getEntity(projectDto)).thenReturn(projectEntity);
        when(projectRepository.existsById(projectEntity.getId())).thenReturn(true);

        assertDoesNotThrow(
                () -> projectService.updateDescription(projectDto, "Finish description"));
    }

    @Test
    public void testGetProjectsFilters() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setName("Name");

        List<Project> projects = new ArrayList<>();

        Project firstProject = new Project();
        Project secondProject = new Project();
        firstProject.setName("Name first");
        secondProject.setName("Name second");

        when(projectRepository.findAll()).thenReturn(projects);
        ProjectServiceImpl service = new ProjectServiceImpl(projectRepository, mapper, filters, validation);

        List<ProjectDto> result = service.getProjectsFilters(filterDto);
        assertThat(result).isEqualTo(projects);
    }

    @Test
    public void testGetProjects() {
        List<ProjectDto> projectsDto = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        ProjectDto firstProjectDto = new ProjectDto();
        ProjectDto secondProjectDto = new ProjectDto();
        Project firstProject = new Project();
        Project secondProject = new Project();
        projects.add(firstProject);
        projects.add(secondProject);
        projectsDto.add(firstProjectDto);
        projectsDto.add(secondProjectDto);

        when(projectRepository.findAll()).thenReturn(projects);
        when(mapper.toDto(firstProject)).thenReturn(firstProjectDto);
        when(mapper.toDto(secondProject)).thenReturn(secondProjectDto);

        List<ProjectDto> result = projectService.getProjects();

        assertEquals(projectsDto, result);
    }

    @Test
    public void testFindById() {
        long id = 1L;
        Project project = new Project();
        project.setId(1L);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        when(validation.findById(id)).thenReturn(project);
        when(mapper.toDto(project)).thenReturn(projectDto);
        ProjectDto result = projectService.findById(id);

        assertEquals(result, projectDto);
    }


}
