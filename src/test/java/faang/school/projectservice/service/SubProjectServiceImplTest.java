package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.CreateSubProjectDtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private List<SubProjectFilter> subProjectFilters;

    @Mock
    private CreateSubProjectDtoValidator validator;

    @Mock
    private MomentRepository momentRepository;

    @InjectMocks
    private SubProjectServiceImpl subProjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSubProject() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
        createSubProjectDto.setParentId(1L);

        Project parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setChildren(Collections.emptyList());

        Project subProject = new Project();
        subProject.setId(2L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(2L);

        when(projectMapper.toEntity(createSubProjectDto)).thenReturn(subProject);
        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(projectMapper.toDTO(subProject)).thenReturn(projectDto);

        ProjectDto result = subProjectService.createSubProject(createSubProjectDto);

        assertEquals(projectDto, result);
        verify(projectRepository, times(1)).save(subProject);
    }

    @Test
    void testUpdateProject() {
        UpdateSubProjectDto updateSubProjectDto = new UpdateSubProjectDto();
        updateSubProjectDto.setId(1L);
        updateSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        Project project = new Project();
        project.setId(1L);
        project.setChildren(Collections.emptyList());
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);
        team.setTeamMembers(new ArrayList<>(List.of(teamMember)));
        project.setTeams(new ArrayList<>(List.of(team)));
        when(projectMapper.toEntity(updateSubProjectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDTO(project)).thenReturn(projectDto);
        when(projectRepository.getProjectById(updateSubProjectDto.getId())).thenReturn(project);

        ProjectDto result = subProjectService.updateProject(updateSubProjectDto);

        assertEquals(projectDto, result);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testGetProjects() {
        SubProjectDtoFilter filters = new SubProjectDtoFilter();
        filters.setProjectStatus(ProjectStatus.CREATED);

        Project project = new Project();
        project.setId(1L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        SubProjectFilter filter = mock(SubProjectFilter.class);
        when(subProjectFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isAcceptable(filters)).thenReturn(true);
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        when(filter.apply(any(), eq(filters))).thenReturn(Stream.of(project));
        when(projectMapper.toDTO(project)).thenReturn(projectDto);

        List<ProjectDto> result = subProjectService.getProjects(filters);

        assertEquals(1, result.size());
        assertEquals(projectDto, result.get(0));
    }

    @Test
    void testIsAllSubProjectsCompleted() {
        UpdateSubProjectDto updateSubProjectDto = new UpdateSubProjectDto();
        updateSubProjectDto.setId(1L);

        Project parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setChildren(Collections.emptyList());

        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);

        boolean result = subProjectService.isAllSubProjectsCompleted(updateSubProjectDto);

        assertTrue(result);
    }

    @Test
    void testCreateMomentForProject() {
        Project project = new Project();
        project.setId(1L);
        project.setTeams(Collections.emptyList());
        Moment moment = new Moment();
        moment.setName("Выполнены все подпроекты");
        moment.setUserIds(new ArrayList<>());
        when(momentRepository.save(any())).thenReturn(moment);

        subProjectService.createMomentForProject(project);

        verify(momentRepository, times(1)).save(any());
    }
}
