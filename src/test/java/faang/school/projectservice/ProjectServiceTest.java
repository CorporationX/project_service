package faang.school.projectservice;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectJpaRepository projectJpaRepository;
    @Mock
    private ProjectFilter filter;
    @Mock
    private Validator validator;
    @InjectMocks
    private ProjectService projectService;
    private ProjectDto firstProjectDto;
    private ProjectDto secondProjectDto;
    private Project firstProjectEntity;
    private Project secondProjectEntity;
    private List<Project> projectList;
    private List<ProjectDto> projectDtoList;
    private ProjectFilterDto filters;
    private Long userId;
    private List<ProjectFilter> projectFilters;
    private Long projectId;


    @BeforeEach
    void setUp() {
        firstProjectDto = new ProjectDto();
        secondProjectDto = new ProjectDto();
        firstProjectEntity = new Project();
        secondProjectEntity = new Project();
        filters = new ProjectFilterDto();
        userId = 1L;

        firstProjectDto.setId(1L);
        firstProjectDto.setOwnerId(userId);
        firstProjectDto.setName("first");
        firstProjectDto.setDescription("first");
        firstProjectDto.setStatus(ProjectStatus.CREATED);

        secondProjectDto.setId(2L);
        secondProjectDto.setOwnerId(userId);
        secondProjectDto.setName("second");
        secondProjectDto.setDescription("second");
        secondProjectDto.setStatus(ProjectStatus.CREATED);

        firstProjectEntity.setId(1L);
        firstProjectEntity.setOwnerId(userId);
        firstProjectEntity.setName("first");
        firstProjectEntity.setDescription("first");
        firstProjectEntity.setStatus(ProjectStatus.CREATED);

        secondProjectEntity.setId(2L);
        secondProjectEntity.setOwnerId(userId);
        secondProjectEntity.setName("second");
        secondProjectEntity.setDescription("first");
        secondProjectEntity.setStatus(ProjectStatus.CREATED);

        projectList = Arrays.asList(firstProjectEntity, secondProjectEntity);
        projectDtoList = Arrays.asList(firstProjectDto, secondProjectDto);
        projectFilters = List.of(new ProjectStatusFilter(), new ProjectNameFilter());
        projectId = 1L;
    }

    @Test
    void testCreate() {
        when(projectMapper.toEntity(firstProjectDto)).thenReturn(firstProjectEntity);
        when(projectRepository.save(firstProjectEntity)).thenReturn(firstProjectEntity);
        when(projectMapper.toDto(firstProjectEntity)).thenReturn(firstProjectDto);

        ProjectDto result = projectService.create(firstProjectDto);

        verify(validator, times(1)).createValidation(firstProjectDto);
        verify(projectRepository, times(1)).save(firstProjectEntity);
        assertEquals(firstProjectDto, result);
    }

    @Test
    void testGetProjectsByFilter() {

        when(projectRepository.findAll()).thenReturn(projectList);
        when(projectMapper.toDtoList(anyList())).thenReturn(projectDtoList);

        List result = projectService.getProjectsByFilter(filters, userId);

        verify(validator, times(1)).userValidator(userId);
        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).toDtoList(anyList());
        assertEquals(projectDtoList, result);
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
    void testGetAllProjects() {
        firstProjectEntity.setVisibility(ProjectVisibility.PUBLIC);
        secondProjectEntity.setVisibility(ProjectVisibility.PRIVATE);
        when(projectMapper.toDto(firstProjectEntity)).thenReturn(firstProjectDto);
        when(projectRepository.findAll()).thenReturn(projectList);
        List<ProjectDto> expectedProjectDtoList = Arrays.asList(firstProjectDto);
        List<ProjectDto> actualProjectDtoList = projectService.getAllProjects(userId);
        //на этом моменте приходит 0 и тест падает
        assertEquals(expectedProjectDtoList.size(), actualProjectDtoList.size());
        assertEquals(expectedProjectDtoList.get(0).getName(), actualProjectDtoList.get(0).getName());
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

