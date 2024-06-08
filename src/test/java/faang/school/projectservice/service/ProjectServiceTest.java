package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectDtoRequest;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.util.TestProject.PROJECT;
import static faang.school.projectservice.util.TestProject.PROJECTS;
import static faang.school.projectservice.util.TestProject.PROJECTS_DTOS;
import static faang.school.projectservice.util.TestProject.PROJECT_DTO;
import static faang.school.projectservice.util.TestProject.PROJECT_DTO_REQUEST;
import static faang.school.projectservice.util.TestProject.PROJECT_ID;
import static faang.school.projectservice.util.TestProject.SAVED_PROJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectValidator projectValidator;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    ProjectMapper projectMapper;
    @InjectMocks
    ProjectService projectService;

    @Test
    public void testCreateProjectIsOwnerPresent() {

        when(projectMapper.requestDtoToProject(any(ProjectDtoRequest.class))).thenReturn(PROJECT);
        lenient().when(projectMapper.projectToDto(any(Project.class))).thenReturn(PROJECT_DTO);
        lenient().when(projectRepository.save(any(Project.class))).thenReturn(SAVED_PROJECT);
        when(projectMapper.projectToDto(any(Project.class))).thenReturn(PROJECT_DTO);

        ProjectDto resultDto = projectService.create(PROJECT_DTO_REQUEST);

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectValidator, times(1)).validateProjectIsUniqByIdAndName(any(Project.class));
        verify(projectMapper, times(1)).projectToDto(any(Project.class));
        verify(projectMapper, times(1)).requestDtoToProject(any(ProjectDtoRequest.class));

        assertNotNull(resultDto);
        assertEquals(PROJECT_DTO_REQUEST.getName(), resultDto.getName());
        assertEquals(PROJECT_DTO_REQUEST.getDescription(), resultDto.getDescription());
        assertEquals(PROJECT_DTO_REQUEST.getOwnerId(), resultDto.getOwnerId());
        assertEquals(ProjectStatus.CREATED, resultDto.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, resultDto.getVisibility());
    }

    @Test
    public void testFindProjectById() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(PROJECT);
        when(projectMapper.projectToDto(PROJECT)).thenReturn(PROJECT_DTO);
        var project = projectRepository.getProjectById(PROJECT_ID);
        var dto = projectMapper.projectToDto(PROJECT);
        verify(projectRepository, times(1)).getProjectById(PROJECT_ID);
        verify(projectMapper, times(1)).projectToDto(PROJECT);
        assertEquals(project.getId(), dto.getId());
        assertEquals(project.getName(), dto.getName());
        assertEquals(project.getDescription(), dto.getDescription());
    }


    @Test
    public void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(PROJECTS);
        when(projectMapper.projectsToDtos(PROJECTS)).thenReturn(PROJECTS_DTOS);
        var projects = projectRepository.findAll();
        var dtos = projectMapper.projectsToDtos(projects);
        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).projectsToDtos(projects);
        assertEquals(projects.size(), dtos.size());
        assertEquals(projects.get(0).getId(), dtos.get(0).getId());
        assertEquals(projects.get(0).getName(), dtos.get(0).getName());
        assertEquals(projects.get(0).getDescription(), dtos.get(0).getDescription());
    }
}