package faang.school.projectservice.projectServiceTests;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.filters.projectFilters.ProjectFilter;
import faang.school.projectservice.mapper.projectMapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectService projectService;

    @Test
    public void testGetProjectById() {
        Long projectId = 1L;

        ProjectResponseDto expectedDto = responseDtoForTests();
        Project project = projectForTests();

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectMapper.toResponseDtoFromEntity(project)).thenReturn(expectedDto);

        ProjectResponseDto result = projectService.getProjectById(projectId);
        verify(projectRepository, Mockito.times(1)).getProjectById(projectId);
        verify(projectMapper, Mockito.times(1)).toResponseDtoFromEntity(project);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Must throw EntityNotFoundException")
    public void testGetProjectByIdThrowEntityNotFoundException() {
        when(projectRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(anyLong()));
    }

    @Test
    public void testFindAllProject() {
        List<Project> expectedProjectList = new ArrayList<>();
        expectedProjectList.add(projectForTests());
        expectedProjectList.add(projectForTests());
        expectedProjectList.add(projectForTests());

        List<ProjectResponseDto> expectedListDto = new ArrayList<>();
        expectedListDto.add(responseDtoForTests());
        expectedListDto.add(responseDtoForTests());
        expectedListDto.add(responseDtoForTests());

        when(projectRepository.findAll()).thenReturn(expectedProjectList);
        when(projectMapper.toResponseDtoFromEntity(any(Project.class))).thenReturn(responseDtoForTests());

        List<ProjectResponseDto> result = projectService.findAllProject();

        verify(projectMapper, times(3)).toResponseDtoFromEntity(any(Project.class));
        verify(projectRepository, times(1)).findAll();

        assertNotNull(result);
        assertEquals(expectedListDto.size(), result.size());
    }

    @Test
    public void testCreateProjectException() {
        ProjectCreateDto testDtoForCreateProject = createTestDtoForCreateProject();

        when(userContext.getUserId()).thenReturn(1L);
        when(projectRepository.existsByOwnerUserIdAndName(1L,
                testDtoForCreateProject.getName()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                projectService.createProject(testDtoForCreateProject));

        verify(projectRepository, times(1))
                .existsByOwnerUserIdAndName(1L, testDtoForCreateProject.getName());
        verify(projectRepository, times(0)).save(any(Project.class));
        verify(projectMapper, times(0)).toEntityFromCreateDto(testDtoForCreateProject);
        verify(projectMapper, times(0)).toResponseDtoFromEntity(any(Project.class));

        assertEquals("Project with the same name already exists.", exception.getMessage());
    }

    @Test
    public void testCreateProjectSuccess() {
        ProjectCreateDto testDtoForCreateProject = createTestDtoForCreateProject();

        when(userContext.getUserId()).thenReturn(1L);
        when(projectRepository.existsByOwnerUserIdAndName(1L, testDtoForCreateProject.getName()))
                .thenReturn(false);

        Project savedProject = new Project();
        savedProject.setName(testDtoForCreateProject.getName());

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponseDto responseDto = new ProjectResponseDto();
        responseDto.setName(savedProject.getName());
        responseDto.setDescription(testDtoForCreateProject.getDescription());

        when(projectMapper.toEntityFromCreateDto(testDtoForCreateProject)).thenReturn(savedProject);
        when(projectMapper.toResponseDtoFromEntity(savedProject)).thenReturn(responseDto);

        ProjectResponseDto response = projectService.createProject(testDtoForCreateProject);

        verify(projectMapper, times(1)).toEntityFromCreateDto(testDtoForCreateProject);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMapper, times(1)).toResponseDtoFromEntity(savedProject);

        assertNotNull(response);
        assertEquals(testDtoForCreateProject.getName(), response.getName());
        assertEquals(testDtoForCreateProject.getDescription(), response.getDescription());
    }


    @Test
    public void testUpdateProject() {
        long projectId = 1L;
        Project existingProject = projectForTests();
        ProjectUpdateDto projectUpdateDto = projectDtoForUpdate();

        doReturn(existingProject).when(projectRepository).getProjectById(projectId);

        doAnswer(invocation -> {
            existingProject.setDescription(projectUpdateDto.getDescription());
            existingProject.setStatus(projectUpdateDto.getStatus());
            existingProject.setUpdatedAt(LocalDateTime.now());
            return existingProject;
        }).when(projectRepository).save(any(Project.class));

        ProjectResponseDto responseDto = ProjectResponseDto.builder()
                .id(projectId)
                .name("Aleksey")
                .description("new DESCRIPTION")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .ownerId(1L)
                .build();
        when(projectMapper.toResponseDtoFromEntity(existingProject)).thenReturn(responseDto);

        ProjectResponseDto response = projectService.updateProject(projectId, projectUpdateDto);

        verify(projectRepository, times(1)).save(existingProject);
        verify(projectMapper, times(1)).toResponseDtoFromEntity(existingProject);

        assertNotNull(response);
        assertEquals(projectUpdateDto.getDescription(), response.getDescription());
        assertEquals(projectUpdateDto.getStatus(), response.getStatus());
        assertEquals(existingProject.getVisibility(), response.getVisibility());
    }

    @Test
    void testFindAllProjectsWithFilters() {
        ProjectFilter filterMock = Mockito.mock(ProjectFilter.class);

        ProjectResponseDto responseDtoForTests = ProjectResponseDto.builder()
                .name("New").build();

        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .name("New")
                .build();

        List<Project> projects = List.of(
                Project.builder().name("New").build(),
                Project.builder().name("second").build()
        );

        when(projectRepository.findAll()).thenReturn(projects);

        when(filterMock.isApplicable(filterDto)).thenReturn(true);
        when(filterMock.apply(any(), eq(filterDto)))
                .thenReturn(projects.stream().filter(project -> "New".equals(project.getName())));

        when(projectMapper.toResponseDtoFromEntity(any(Project.class))).thenReturn(responseDtoForTests);

        ProjectService projectService = new ProjectService(
                projectRepository, projectMapper, userContext, List.of(filterMock));

        List<ProjectResponseDto> projectResponseDtos = projectService.findAllProjectsWithFilters(filterDto);

        verify(projectMapper).toResponseDtoFromEntity(any(Project.class));

        verify(filterMock).isApplicable(filterDto);
        verify(filterMock).apply(any(), eq(filterDto));

        assertEquals(1, projectResponseDtos.size());
        assertEquals("New", projectResponseDtos.get(0).getName());
    }

    private ProjectUpdateDto projectDtoForUpdate() {
        return ProjectUpdateDto.builder()
                .description("new DESCRIPTION")
                .status(ProjectStatus.COMPLETED)
                .build();
    }

    private Project projectForTests() {
        return Project.builder()
                .id(1L)
                .name("Aleksey")
                .description("New Task")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .ownerId(1L)
                .build();
    }

    private ProjectResponseDto responseDtoForTests() {
        return ProjectResponseDto.builder()
                .id(1L)
                .name("Aleksey")
                .description("New Task")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .ownerId(1L)
                .build();
    }

    private ProjectCreateDto createTestDtoForCreateProject() {
        return ProjectCreateDto.builder()
                .ownerId(1L)
                .name("new")
                .description("new for test")
                .build();
    }
}
