package faang.school.projectservice;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator projectValidator;
    @InjectMocks
    private ProjectService projectService;

    private ProjectDto firstProjectDto;
    private ProjectDto secondProjectDto;
    private Project firstProjectEntity;
    private Project secondProjectEntity;
    private List<Project> projectList;
    private List<ProjectDto> projectDtoList;
    private Long userId;
    private Long projectId;
    private TeamMember teamMember;
    private ProjectFilterDto filters;


    @BeforeEach
    void setUp() {
        firstProjectDto = new ProjectDto();
        secondProjectDto = new ProjectDto();
        firstProjectEntity = new Project();
        secondProjectEntity = new Project();
        userId = 1L;

        firstProjectDto.setId(1L);
        firstProjectDto.setOwnerId(userId);
        firstProjectDto.setName("first");
        firstProjectDto.setDescription("first");
        firstProjectDto.setStatus(ProjectStatus.CREATED);
        firstProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        secondProjectDto.setId(2L);
        secondProjectDto.setOwnerId(userId);
        secondProjectDto.setName("second");
        secondProjectDto.setDescription("second");
        secondProjectDto.setStatus(ProjectStatus.CREATED);
        secondProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        firstProjectEntity.setId(1L);
        firstProjectEntity.setOwnerId(userId);
        firstProjectEntity.setName("first");
        firstProjectEntity.setDescription("first");
        firstProjectEntity.setStatus(ProjectStatus.CREATED);
        firstProjectEntity.setVisibility(ProjectVisibility.PUBLIC);

        secondProjectEntity.setId(2L);
        secondProjectEntity.setOwnerId(userId);
        secondProjectEntity.setName("second");
        secondProjectEntity.setDescription("first");
        secondProjectEntity.setStatus(ProjectStatus.CREATED);
        secondProjectEntity.setVisibility(ProjectVisibility.PRIVATE);

        projectList = Arrays.asList(firstProjectEntity, secondProjectEntity);
        projectDtoList = Arrays.asList(firstProjectDto, secondProjectDto);
        projectId = 1L;
    }

    @Test
    void testCreate() {
        when(projectMapper.toEntity(firstProjectDto)).thenReturn(firstProjectEntity);
        when(projectRepository.save(firstProjectEntity)).thenReturn(firstProjectEntity);
        when(projectMapper.toDto(firstProjectEntity)).thenReturn(firstProjectDto);

        ProjectDto result = projectService.createProject(firstProjectDto);

        verify(projectValidator, times(1)).checkIfProjectExists(firstProjectDto);
        verify(projectRepository, times(1)).save(firstProjectEntity);
        assertEquals(firstProjectDto, result);
    }

    @Test
    void testGetProjectsByFilter() {
        List<ProjectFilter> projectFilterList = List.of(new ProjectNameFilter(), new ProjectStatusFilter());
        projectService = new ProjectService(projectRepository, projectMapper, projectValidator, projectFilterList);

        filters = ProjectFilterDto.builder().namePattern("first").statusPattern(ProjectStatus.CREATED).build();
        when(projectRepository.findAll()).thenReturn(List.of(firstProjectEntity, secondProjectEntity));

        List<ProjectDto> projectsByStatus = projectService.getProjectsByFilter (
                ProjectFilterDto.builder().namePattern("first").build());
        assertEquals(1, projectsByStatus.size());
    }

    @Test
    void testUpdateProject() {
        when(projectRepository.getProjectById(projectId)).thenReturn(firstProjectEntity);
        when(projectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(projectMapper.toDto(firstProjectEntity)).thenReturn(firstProjectDto);
        ProjectDto updatedProjectDto = projectService.updateProject(projectId, firstProjectDto);
        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(projectRepository, times(1)).save(any());
        assertEquals(firstProjectDto.getName(), updatedProjectDto.getName());
        assertEquals(firstProjectDto.getDescription(), updatedProjectDto.getDescription());
    }

    @Test
    void testGetAllProjectsIsEmpty() {
        assertEquals(new ArrayList<>(), projectService.getAllProjects());
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.getProjectById(1L)).thenReturn(new Project());
        when(projectMapper.toDto(any(Project.class))).thenReturn(new ProjectDto());
        ProjectDto projectDto = projectService.getProjectById(1L);
        verify(projectRepository, times(1)).getProjectById(1L);
        verify(projectMapper, times(1)).toDto(any(Project.class));
        assertNotNull(projectDto);
    }
}
