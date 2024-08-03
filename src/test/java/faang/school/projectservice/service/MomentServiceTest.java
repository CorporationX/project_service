package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private MomentMapper momentMapper;

    @Mock
    private MomentServiceValidator momentServiceValidator;

    @Mock
    private List<MomentFilter> momentFilters;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MomentService momentService;

    private MomentDto momentDto;
    private Moment moment;
    private Project project;
    private Team team;
    private TeamMember teamMember;

    @BeforeEach
    public void setUp() {
        momentDto = MomentDto.builder()
                .id(1L)
                .projectsIds(Arrays.asList(1L))
                .userIds(Arrays.asList(1L))
                .description("description")
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .build();

        team = Team.builder()
                .id(1L)
                .teamMembers(List.of(teamMember))
                .build();

        project = Project.builder()
                .id(1L)
                .teams(List.of(team))
                .build();

        moment = Moment.builder().id(1L)
                .projects(List.of(project))
                .userIds(List.of(1L, 2L, 3L))
                .description("description")
                .build();
    }

    @Test
    @DisplayName("Test createMoment")
    public void testCreateMoment() {
        doNothing().when(momentServiceValidator).validateCreateMoment(any(MomentDto.class));
        when(momentMapper.toEntity(any(MomentDto.class))).thenReturn(moment);
        momentService.createMoment(momentDto);

        verify(momentServiceValidator).validateCreateMoment(momentDto);
        verify(momentRepository).save(moment);
    }

    @Test
    @DisplayName("Test updateMoment")
    public void testUpdateMoment() {
        when(momentRepository.findById(momentDto.getId())).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);
        when(projectService.findDifferentProjects(anyList(), anyList())).thenReturn(Collections.emptyList());
        when(userService.findDifferentMemberIds(anyList(), anyList())).thenReturn(Collections.emptyList());
        momentService.updateMoment(momentDto);

        verify(momentRepository).save(moment);
    }

    @Test
    @DisplayName("Test updateMoment - Moment Not Found")
    public void testUpdateMomentNotFoundMoment() {
        when(momentRepository.findById(momentDto.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> momentService.updateMoment(momentDto));
        assertEquals("Moment not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test getAllMomentsWithFilters - No Filters")
    public void getAllMomentsWithNoFilters() {
        MomentFilterDto momentFilterDto = new MomentFilterDto();
        when(momentRepository.findAll()).thenReturn(List.of(moment));
        when(momentFilters.stream()).thenReturn(Stream.of());

        List<MomentDto> result = momentService.getFilteredMoments(momentFilterDto);

        verify(momentFilters, times(1)).stream();
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test getAllMoments with applicable filters")
    public void testGetAllMomentsWithApplicableFilters() {
        momentService.getFilteredMoments(new MomentFilterDto());

        verify(momentRepository, times(1)).findAll();
    }

    @Test
    public void getAllMomentsReturnAllMoments() {
        when(momentRepository.findAll()).thenReturn(List.of(moment));
        when(momentMapper.toDto(anyList())).thenReturn(List.of(momentDto));

        List<MomentDto> result = momentService.getAllMoments();

        assertEquals(1, result.size());
        verify(momentRepository, times(1)).findAll();
    }

    @Test
    public void getMomentByIdReturnMomentDto() {
        when(momentRepository.findById(any(Long.class))).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(any(Moment.class))).thenReturn(momentDto);

        MomentDto result = momentService.getMomentById(1L);

        assertEquals(momentDto, result);
        verify(momentRepository, times(1)).findById(any(Long.class));
    }

    @Test
    public void getMomentById_invalidId_shouldThrowException() {
        when(momentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> momentService.getMomentById(1L));
    }
}
