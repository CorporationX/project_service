package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    @InjectMocks
    private ProjectServiceImpl projectServiceImpl;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectFilterService projectFilterService;
    @Captor
    private ArgumentCaptor<Project> captorForProject;
    @Captor
    private ArgumentCaptor<Stream<Project>> captorForStreamOfProject;

    private ProjectDto firstProjectDto;
    private Project secondProject;
    private Project thirdProject;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    void init() {
        firstProjectDto = new ProjectDto();
        firstProjectDto.setId(1L);
        firstProjectDto.setName("First project");

        secondProject = new Project();
        secondProject.setId(1L);
        secondProject.setName("Second project");
        secondProject.setVisibility(ProjectVisibility.PUBLIC);

        thirdProject = new Project();
        thirdProject.setId(3L);
        thirdProject.setName("Third project");
        thirdProject.setVisibility(ProjectVisibility.PRIVATE);
        thirdProject.setTeams(List.of(Team.builder().teamMembers(List.of(TeamMember.builder().id(3L).build())).build()));

        projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setName("Project");
        projectFilterDto.setStatus(ProjectStatus.CREATED);
    }

    @Test
    public void testCreateWithSaving() {
        projectServiceImpl.create(firstProjectDto);
        verify(projectRepository, times(1)).save(captorForProject.capture());

        Project capturedProject = captorForProject.getValue();
        assertEquals(capturedProject.getId(), firstProjectDto.getId());
        assertEquals(capturedProject.getName(), firstProjectDto.getName());
    }

    @Test
    public void testCreateWithSettingStatus() {
        projectServiceImpl.create(firstProjectDto);
        verify(projectValidator, times(1)).validateProjectByOwnerIdAndNameOfProject(firstProjectDto);

        assertEquals(ProjectStatus.CREATED, firstProjectDto.getStatus());
    }

    @Test
    public void testCreateWithSettingOwnerId() {
        when(userContext.getUserId()).thenReturn(1L);
        projectServiceImpl.create(firstProjectDto);
        verify(projectValidator, times(1)).validateProjectByOwnerIdAndNameOfProject(firstProjectDto);

        assertEquals(1L, firstProjectDto.getOwnerId());
    }

    @Test
    public void testCreateWithCreating() {
        Project firstProject = projectMapper.toProject(firstProjectDto);
        when(projectRepository.save(any(Project.class))).thenReturn(firstProject);
        ProjectDto result = projectServiceImpl.create(firstProjectDto);

        assertEquals(result.getId(), firstProject.getId());
        assertEquals(result.getName(), firstProject.getName());
    }

    @Test
    public void testGetAllWithGetting() {
        List<Project> projects = List.of(projectMapper.toProject(firstProjectDto));
        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectDto> result = projectServiceImpl.getAll();
        assertEquals(result, projectMapper.toDtos(projects));

        InOrder inOrder = inOrder(projectRepository, projectMapper);
        inOrder.verify(projectRepository, times(1)).findAll();
        inOrder.verify(projectMapper, times(1)).toDtos(projects);
    }

    @Test
    public void testFindByIdWithFinding() {
        Project project = projectMapper.toProject(firstProjectDto);
        when(projectRepository.getProjectById(firstProjectDto.getId())).thenReturn(project);

        ProjectDto result = projectServiceImpl.findById(firstProjectDto.getId());
        assertEquals(result, firstProjectDto);

        InOrder inOrder = inOrder(projectRepository, projectMapper);
        inOrder.verify(projectRepository, times(1)).getProjectById(1L);
        inOrder.verify(projectMapper, times(1)).toDto(project);
    }

    @Test
    public void testUpdateWithUpdating() {
        when(projectRepository.getProjectById(firstProjectDto.getId())).thenReturn(secondProject);
        when(projectRepository.save(secondProject)).thenReturn(secondProject);
        ProjectDto result = projectServiceImpl.update(firstProjectDto);
        assertEquals(result.getName(), firstProjectDto.getName());
        assertEquals(result.getId(), firstProjectDto.getId());

        InOrder inOrder = inOrder(projectRepository, projectMapper);
        inOrder.verify(projectRepository, times(1)).getProjectById(1L);
        inOrder.verify(projectMapper, times(1)).updateProject(firstProjectDto, secondProject);
        inOrder.verify(projectMapper, times(1)).toDto(secondProject);
    }

    @Test
    public void testGetAllByFilterWithFilteringPublicProject() {
        List<Project> projects = List.of(secondProject);
        when(projectRepository.findAll()).thenReturn(projects);
        projectServiceImpl.getAllByFilter(projectFilterDto);
        verify(projectFilterService, times(1)).applyFilters(captorForStreamOfProject.capture(), any(ProjectFilterDto.class));
        var result = captorForStreamOfProject.getValue().toList().get(0);
        assertEquals(result, secondProject);
        assertEquals(result.getName(), secondProject.getName());
        assertEquals(result.getId(), secondProject.getId());
        assertEquals(result.getVisibility(), ProjectVisibility.PUBLIC);
    }

    @Test
    public void testGetAllByFilterWithFilteringPrivateProjectAndMissing() {
        List<Project> projects = List.of(thirdProject);
        when(projectRepository.findAll()).thenReturn(projects);
        when(userContext.getUserId()).thenReturn(4L);
        projectServiceImpl.getAllByFilter(projectFilterDto);
        verify(projectFilterService, times(1)).applyFilters(captorForStreamOfProject.capture(), any(ProjectFilterDto.class));
        var result = captorForStreamOfProject.getValue().count();
        assertEquals(result, 0);
    }

    @Test
    public void testGetAllByFilterWithFilteringPresentingPrivateProject() {
        List<Project> projects = List.of(thirdProject);
        when(projectRepository.findAll()).thenReturn(projects);
        when(userContext.getUserId()).thenReturn(3L);
        projectServiceImpl.getAllByFilter(projectFilterDto);

        verify(projectRepository, times(1)).findAll();
        verify(projectFilterService, times(1)).applyFilters(captorForStreamOfProject.capture(), any(ProjectFilterDto.class));

        var result = captorForStreamOfProject.getValue().toList();
        assertEquals(1, result.size());
        assertEquals(result.get(0), thirdProject);
        assertEquals(result.get(0).getId(), thirdProject.getId());
    }
}
