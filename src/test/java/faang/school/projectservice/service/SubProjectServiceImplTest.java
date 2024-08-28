package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SubProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SubProjectMapper subProjectMapper;

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

        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setId(2L);

        when(subProjectMapper.toEntity(createSubProjectDto)).thenReturn(subProject);
        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(subProjectMapper.toDTO(subProject)).thenReturn(subProjectDto);

        SubProjectDto result = subProjectService.createSubProject(createSubProjectDto);

        assertEquals(subProjectDto, result);
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
        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setId(1L);
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);
        team.setTeamMembers(new ArrayList<>(List.of(teamMember)));
        project.setTeams(new ArrayList<>(List.of(team)));
        when(subProjectMapper.toEntity(updateSubProjectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(subProjectMapper.toDTO(project)).thenReturn(subProjectDto);
        when(projectRepository.getProjectById(updateSubProjectDto.getId())).thenReturn(project);

        SubProjectDto result = subProjectService.updateProject(updateSubProjectDto);

        assertEquals(subProjectDto, result);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testGetProjects() {
        SubProjectDtoFilter filters = new SubProjectDtoFilter();
        filters.setProjectStatus(ProjectStatus.CREATED);

        Project project = new Project();
        project.setId(1L);
        project.setChildren(new ArrayList<>(List.of(new Project())));
        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setId(1L);

        SubProjectFilter filter = mock(SubProjectFilter.class);
        when(subProjectFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isAcceptable(filters)).thenReturn(true);
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        when(filter.apply(any(), eq(filters))).thenReturn(Stream.of(project));
        when(subProjectMapper.toDTO(project)).thenReturn(subProjectDto);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        List<SubProjectDto> result = subProjectService.getProjects(filters, 1L);

        assertEquals(1, result.size());
        assertEquals(subProjectDto, result.get(0));
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
