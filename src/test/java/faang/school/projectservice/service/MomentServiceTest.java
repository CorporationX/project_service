package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

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
    private ProjectRepository projectRepository;

    @InjectMocks
    private MomentService momentService;

    private MomentDto momentDto;
    private Moment moment;
    private Project project;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        momentDto = new MomentDto();
        momentDto.setProjectsIDs(List.of(1L));

        moment = new Moment();
        project = new Project();
        Team team = new Team();
        TeamMember teamMemberFirst = new TeamMember();
        teamMemberFirst.setId(1L);
        TeamMember teamMemberSecond = new TeamMember();
        teamMemberSecond.setId(2L);

        team.setTeamMembers(List.of(teamMemberFirst, teamMemberSecond));
        project.setTeams(List.of(team));
    }

    

    @Test
    @DisplayName("Test retrieval of all moments with filters")
    public void testGetAllMomentsWithFilters() {
        MomentFilterDto momentFilterDto = new MomentFilterDto();
        when(momentRepository.findAll()).thenReturn(List.of(moment));
        when(momentMapper.toDto(anyList())).thenReturn(List.of(momentDto));

        List<MomentDto> moments = momentService.getAllMoments(momentFilterDto);

        assertEquals(1, moments.size());
        verify(momentRepository).findAll();
    }

    @Test
    @DisplayName("Test retrieval of all moments without filters")
    public void testGetAllMoments() {
        when(momentRepository.findAll()).thenReturn(List.of(moment));
        when(momentMapper.toDto(anyList())).thenReturn(List.of(momentDto));

        List<MomentDto> moments = momentService.getAllMoments();

        assertEquals(1, moments.size());
        verify(momentRepository).findAll();
    }

    @Test
    @DisplayName("Test retrieval of a moment by ID")
    public void testGetMomentById() {
        when(momentRepository.findById(anyLong())).thenReturn(Optional.of(moment));
        doNothing().when(momentServiceValidator).validateGetMomentById(any(Moment.class));
        when(momentMapper.toDto(any(Moment.class))).thenReturn(momentDto);

        MomentDto result = momentService.getMomentById(1L);

        assertNotNull(result);
        verify(momentRepository).findById(1L);
    }
}
