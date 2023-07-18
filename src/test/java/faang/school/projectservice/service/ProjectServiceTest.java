package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectFilterStatus;
import faang.school.projectservice.filter.project.ProjectTitleFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    @InjectMocks
    private ProjectService projectService;
    ProjectDto projectDto;
    Project project;
    Project project1;
    Project project2;
    Project project3;

    @BeforeEach
    public void init() {
        ProjectTitleFilter projectTitleFilter = new ProjectTitleFilter();
        ProjectFilterStatus projectFilterStatus = new ProjectFilterStatus();
        List<ProjectFilter> projectFilters = List.of(projectTitleFilter, projectFilterStatus);
        projectService = new ProjectService(projectRepository, projectMapper, projectFilters);
        projectDto = ProjectDto.builder().id(1L).privateProject(false).createdAt(LocalDateTime.now()).description("s").name("q").ownerId(1L).build();
        project = Project.builder().id(1L).createdAt(LocalDateTime.now()).description("s").name("q").build();

        project1 = Project.builder().id(1L).createdAt(LocalDateTime.now()).description("s").name("CorporationX").status(ProjectStatus.CREATED).build();
        project2 = Project.builder().id(2L).createdAt(LocalDateTime.now()).description("b").name("CorporationX").status(ProjectStatus.ON_HOLD).build();
        project3 = Project.builder().id(3L).createdAt(LocalDateTime.now()).description("a").name("Facebook").status(ProjectStatus.CREATED).build();
    }

    @Test
    public void testCreateProjectThrowsException() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    public void testCreateProject() {
        ProjectDto projectDto1 = ProjectDto.builder().id(1L).privateProject(true).createdAt(LocalDateTime.now()).description("s").name("q").ownerId(1L).status(ProjectStatus.CREATED).build();
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenReturn(project);
        Mockito.when(projectMapper.toProject(projectDto)).thenReturn(project);
        Mockito.when(projectMapper.toProjectDto(project)).thenReturn(projectDto1);
        assertEquals(ProjectStatus.CREATED, projectService.createProject(projectDto).getStatus());
    }

    @Test
    public void testUpdateProject() {
        ProjectDto projectDtoForUpdate = ProjectDto.builder().id(1L).privateProject(true).createdAt(LocalDateTime.now()).description("new description").name("q").ownerId(1L).status(ProjectStatus.CREATED).build();
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(project);
        Mockito.when(projectMapper.toProjectDto(project)).thenReturn(projectDto);
        projectService.updateProject(1L, projectDtoForUpdate);
        assertEquals("new description", projectDto.getDescription());
    }

    @Test
    public void testGetFilteredProjectsByTitle() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));

        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().name("CorporationX").build();

        List<ProjectDto> projectDtoList = projectService.getProjectByFilter(projectFilterDto);
        assertEquals(2, projectDtoList.size());
    }

    @Test
    public void testGetFilteredProjectsByStatus() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));

        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().status(ProjectStatus.CREATED).build();

        List<ProjectDto> projectDtoList = projectService.getProjectByFilter(projectFilterDto);
        assertEquals(2, projectDtoList.size());
    }

    @Test
    public void testGetAllProjects() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));
        List<ProjectDto> projectDtoList = projectService.getAllProjects();
        assertEquals(3, projectDtoList.size());
    }

    @Test
    public void testGetProjectById() {
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);
        assertEquals(projectMapper.toProjectDto(project), projectService.getProjectById(1L));
    }

    @Test
    public void testGetProjectByIdThrowsException() {
        Mockito.when(projectRepository.getProjectById(-1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(-1L));
    }
}