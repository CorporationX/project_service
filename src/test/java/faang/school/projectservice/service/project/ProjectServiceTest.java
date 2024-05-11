package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.NoProjectsInTheDatabase;
import faang.school.projectservice.exception.ProjectAlreadyExistsException;
import faang.school.projectservice.exception.ProjectDoesNotExistInTheDatabase;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private final Long PROJECT_ID = 1L;
    private final String PROJECT_NAME = "Name";
    private final String PROJECT_DESCRIPTION = "Description";

    @Mock
    public ProjectRepository projectRepository;

    @Mock
    public ProjectMapper projectMapper;


    @InjectMocks
    public ProjectService projectService;

    public ProjectDto projectDto;
    public Project project;

    @BeforeEach
    void setUp() {
        projectDto = new ProjectDto();
        projectDto.setOwnerId(PROJECT_ID);
        projectDto.setName(PROJECT_NAME);
        projectDto.setDescription(PROJECT_DESCRIPTION);
        project = new Project();
        project.setId(projectDto.getId());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
    }

    @Test
    public void testCreateProjectException() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(true);
        assertThrows(ProjectAlreadyExistsException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    public void testCreateProjectAllGood() {
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.createProject(projectDto);
        assertEquals(actual, projectDto);
    }

    @Test
    public void testUpdateProjectException(){
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(true);
        assertThrows(ProjectDoesNotExistInTheDatabase.class, () -> projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, PROJECT_DESCRIPTION));
    }

    @Test
    void updateProject_setsUpdatedAtToNow() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(false);
        when(projectMapper.toProject(any())).thenReturn(project);
        when(projectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, "New Description");
        assertNotNull(project.getUpdatedAt());
        assertTrue(project.getUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    public void testUpdateProjectAllGood(){
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, PROJECT_DESCRIPTION);
        assertEquals(actual, projectDto);
    }

    @Test
    public void testUpdateProjectWithNullDescription() {
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, null);
        assertEquals(actual, projectDto);
    }
    @Test
    public void testUpdateProjectWithEmptyDescription() {
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, "");
        assertEquals(actual, projectDto);
    }

    @Test
    public void testGetAllProjectsException(){
        when(projectRepository.findAll()).thenReturn(null);
        assertThrows(NoProjectsInTheDatabase.class, () -> projectService.getAllProjects());
    }

    @Test
    void getAllProjects_whenProjectsExist_returnsDtoList() {
        List<Project> projects = List.of(project);
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(projects)).thenReturn(List.of(projectDto));
        List<ProjectDto> projectDtos = projectService.getAllProjects();
        assertNotNull(projectDtos);
        assertEquals(1, projectDtos.size());
        assertEquals(projectDto, projectDtos.get(0));
    }
}