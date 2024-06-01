package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.util.TestProject.PROJECT;
import static faang.school.projectservice.util.TestProject.PROJECTS;
import static faang.school.projectservice.util.TestProject.PROJECTS_DTOS;
import static faang.school.projectservice.util.TestProject.PROJECT_DTO;
import static faang.school.projectservice.util.TestProject.PROJECT_ID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectValidator projectValidator;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    UserContext userContext;
    @Mock
    ProjectMapper projectMapper;
    @InjectMocks
    ProjectService projectService;

    @Test
    public void testCreateProjectIsOwnerPresent() {
        when(projectRepository.save(PROJECT)).thenReturn(PROJECT);
        when(projectMapper.projectToDto(PROJECT)).thenReturn(PROJECT_DTO);
        var project = projectRepository.save(PROJECT);
        var dto = projectMapper.projectToDto(PROJECT);
//        var rrr = projectService.create(new ProjectCreateDto());
        verify(projectRepository, times(1)).save(PROJECT);
        verify(projectMapper, times(1)).projectToDto(PROJECT);
    }

    @Test
    public void testCreateProjectIsOwnerNotPresent() {
        // Используем пользователя из контекста
    }

    @Test
    public void testUpdateProject() {

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
    public void testIsUserExistInTeams() {


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