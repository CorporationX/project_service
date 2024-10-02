package faang.school.projectservice.service.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.TeamMemberServiceImpl;
import faang.school.projectservice.service.teamfilter.TeamMemberFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    private final static long USER_ID = 1L;
    private final static long TEAM_ID = 5L;
    private final static long PROJECT_ID = 6L;


    @InjectMocks
    private TeamMemberServiceImpl teamMemberService;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Spy
    private TeamMemberMapper teamMemberMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private List<TeamMemberFilter> teamMemberFilters;
    @Mock
    private UserContext userContext;


    private TeamMemberDto teamMemberDto;
    private TeamMember teamMember;
    private TeamMember owner;
    private TeamMember teamlead;
    private Pageable pageable;
    private Team team;
    private Project project;

    @BeforeEach
    public void setUp() {

        team = new Team();
        team.setId(TEAM_ID);
        project = new Project();
        project.setId(PROJECT_ID);

        team.setProject(project);

        teamMember = new TeamMember();
        teamMember.setId(2L);
        teamMember.setRoles(List.of());
        teamMember.setTeam(team);

        owner = new TeamMember();
        owner.setId(3L);
        owner.setRoles(List.of(TeamRole.OWNER));
        owner.setTeam(team);

        teamlead = new TeamMember();
        teamlead.setId(7L);
        teamlead.setRoles(List.of(TeamRole.TEAMLEAD));
        teamlead.setTeam(team);

        team.setTeamMembers(List.of(teamMember, owner, teamlead));
        project.setTeams(List.of(team));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    public void testAddTeamMemberSuccess() {

        teamMemberDto = new TeamMemberDto(2L, List.of(TeamRole.DEVELOPER));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamlead);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(2L, PROJECT_ID)).thenReturn(teamMember);
        when(teamMemberMapper.toTeamMember(teamMemberDto)).thenReturn(teamMember);

        TeamMemberDto result = teamMemberService.addTeamMember(TEAM_ID, teamMemberDto);

        assertNotNull(teamMemberDto);
        verify(teamMemberJpaRepository, times(1)).save(any(TeamMember.class));
    }

    @Test
    public void testAddTeamMemberWhenUserNotAuthorized() {
        TeamMember user = new TeamMember();
        user.setRoles(List.of(TeamRole.ANALYST));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(user);

        assertThrows(DataValidationException.class, () -> teamMemberService.addTeamMember(TEAM_ID, teamMemberDto));
    }

    @Test
    public void testAddTeamMemberShouldThrowExceptionWhenTeamMemberRoleIsNotEmpty() {

        teamMemberDto = new TeamMemberDto(2L, List.of(TeamRole.DEVELOPER));
        teamMember.setRoles(List.of(TeamRole.DESIGNER));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamlead);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(2L, PROJECT_ID)).thenReturn(teamMember);

        assertThrows(DataValidationException.class, () -> teamMemberService.addTeamMember(TEAM_ID, teamMemberDto));
    }

    @Test
    public void testUpdateTeamMemberShouldUpdateWhenMemberExists() {
        teamMemberDto = new TeamMemberDto(2L, List.of(TeamRole.DEVELOPER));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamlead);
        when(teamMemberMapper.toTeamMember(teamMemberDto)).thenReturn(teamMember);

        TeamMemberDto result = teamMemberService.updateTeamMember(TEAM_ID, teamMemberDto);

        assertNotNull(teamMemberDto);
        verify(teamMemberJpaRepository).save(teamMember);
    }

    @Test
    public void testUpdateTeamMemberShouldThrowExceptionWhenUserRolesIsIncorrect() {

        TeamMember member = new TeamMember();
        member.setRoles(List.of(TeamRole.DEVELOPER));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));

        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(member);

        assertThrows(DataValidationException.class, () -> teamMemberService.updateTeamMember(TEAM_ID, teamMemberDto));
    }

    @Test
    public void testDeleteTeamMemberShouldDeleteWhenMemberExists() {

        when(teamMemberRepository.findById(2L)).thenReturn(teamMember);
        teamMemberService.deleteTeamMember(teamMember.getId());

        verify(teamMemberJpaRepository, times(1)).delete(teamMember);
    }

    @Test
    public void testGetTeamMembersByFilterWhenNoFilters() {

        when(teamMemberJpaRepository.findAll()).thenReturn(List.of(teamMember));
        when(teamMemberFilters.stream()).thenReturn(Stream.empty());
        when(teamMemberMapper.toTeamMemberDto(teamMember)).thenReturn(teamMemberDto);

        List<TeamMemberDto> result = teamMemberService.getTeamMembersByFilter(TEAM_ID, new TeamFilterDto());

        assertEquals(1, result.size());
        assertEquals(teamMemberDto, result.get(0));
    }

    @Test
    public void testGetTeamMembersByFilterWithApplicableFilters() {

        when(teamMemberJpaRepository.findAll()).thenReturn(List.of(teamMember));

        when(teamMemberMapper.toTeamMemberDto(teamMember)).thenReturn(teamMemberDto);

        TeamFilterDto filter = new TeamFilterDto();
        filter.setTeamRole(TeamRole.DEVELOPER);

        List<TeamMemberDto> result = teamMemberService.getTeamMembersByFilter(TEAM_ID, filter);

        assertEquals(1, result.size());
        assertEquals(teamMemberDto, result.get(0));
    }

        @Test
    public void testGetAllTeamMembersShouldReturnAllMembers() {
        List<TeamMember> members = List.of(teamMember);
        Page<TeamMember> teamMembersPage = new PageImpl<>(members);
        when(teamMemberJpaRepository.findAll(pageable)).thenReturn(teamMembersPage);

        Page<TeamMemberDto> result = teamMemberService.getAllTeamMembers(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetTeamMemberByIdShouldReturnMemberWhenExists() {
        long teamMemberId = 1L;
        when(teamMemberMapper.toTeamMemberDto(any())).thenReturn(teamMemberDto);

        TeamMemberDto result = teamMemberService.getTeamMemberById(teamMemberId);

        assertThat(result).isEqualTo(teamMemberDto);
    }
}
