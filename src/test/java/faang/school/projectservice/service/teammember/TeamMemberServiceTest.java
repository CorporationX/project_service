package faang.school.projectservice.service.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

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
    private List<TeamMemberFilter> teamMemberFilters;
    @Mock
    private UserContext userContext;


    private TeamMemberDto teamMemberDto;
    private TeamMember teamMember;
    private Pageable pageable;

    private final static long USER_ID = 1L;
    private final static long TEAM_ID = 2L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        teamMemberDto = new TeamMemberDto(2L, List.of(TeamRole.DEVELOPER));
        teamMember = TeamMember.builder()
                .userId(teamMemberDto.teamMemberId())
                .roles(teamMemberDto.roles())
                .build();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    public void testAddTeamMemberShouldAddMemberWhenUserIsAuthorized() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        TeamMember teamlead = TeamMember.builder()
                .userId(USER_ID)
                .roles(List.of(TeamRole.TEAMLEAD))
                .build();
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, TEAM_ID)).thenReturn(teamlead);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(2L, TEAM_ID)).thenReturn(new TeamMember());
        when(teamMemberMapper.toTeamMemberDto(any())).thenReturn(teamMemberDto);

        TeamMemberDto result = teamMemberService.addTeamMember(TEAM_ID, teamMemberDto);

        assertThat(result).isEqualTo(teamMemberDto);
        verify(teamMemberJpaRepository).save(any(TeamMember.class));
    }

    @Test
    public void testAddTeamMemberShouldThrowExceptionWhenUserIsNotAuthorized() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, TEAM_ID)).thenReturn(new TeamMember());

        assertThrows(DataValidationException.class, () -> teamMemberService.addTeamMember(TEAM_ID, teamMemberDto));
    }

    @Test
    public void testAddTeamMemberShouldThrowExceptionWhenUserRoleIsIncorrect() {
        long userId = 1L;
        TeamMember developer = new TeamMember();
        developer.setRoles(List.of(TeamRole.DEVELOPER));
        when(teamMemberRepository.findById(userId)).thenReturn(developer);

        assertThrows(DataValidationException.class, () -> teamMemberService.addTeamMember(userId, teamMemberDto));
    }

    @Test
    public void testAddTeamMemberShouldThrowExceptionWhenUserRoleIsNotEmpty() {
        long userId = 1L;
        when(teamMemberDto.roles()).thenReturn(List.of(any()));

        assertThrows(DataValidationException.class, () -> teamMemberService.addTeamMember(userId, teamMemberDto));
    }

    @Test
    public void testUpdateTeamMemberShouldUpdateWhenMemberExists() {
        long userId = 2L;
        teamMemberDto = new TeamMemberDto(1L, List.of(TeamRole.DESIGNER));
        when(teamMemberMapper.toTeamMember(teamMemberDto)).thenReturn(teamMember);
        when(teamMemberJpaRepository.save(any())).thenReturn(teamMember);
        when(teamMemberMapper.toTeamMemberDto(any())).thenReturn(teamMemberDto);

        TeamMemberDto result = teamMemberService.updateTeamMember(userId, teamMemberDto);

        assertThat(result).isEqualTo(teamMemberDto);
        verify(teamMemberJpaRepository).save(teamMember);
    }

    @Test
    public void testUpdateTeamMemberShouldThrowExceptionWhenUserRolesIsIncorrect() {
        long userId = 1L;
        TeamMember developer = new TeamMember();
        developer.setRoles(List.of(TeamRole.DEVELOPER));
        when(teamMemberRepository.findById(userId)).thenReturn(developer);

        assertThrows(DataValidationException.class, () -> teamMemberService.updateTeamMember(userId, teamMemberDto));
    }

    @Test
    public void testDeleteTeamMemberShouldDeleteWhenMemberExists() {

        when(teamMemberRepository.findById(USER_ID)).thenReturn(teamMember);
        teamMemberService.deleteTeamMember(USER_ID, TEAM_ID);

        verify(teamMemberJpaRepository, times(1)).delete(teamMember);
    }

    @Test
    public void testGetTeamMembersByFilterWhenNoFilters() {
        long teamId = 1L;
        when(teamMemberJpaRepository.findAll()).thenReturn(List.of(teamMember));
        when(teamMemberFilters.stream()).thenReturn(Stream.empty());
        when(teamMemberMapper.toTeamMemberDto(teamMember)).thenReturn(teamMemberDto);

        List<TeamMemberDto> result = teamMemberService.getTeamMembersByFilter(teamId, new TeamFilterDto());

        assertEquals(1, result.size());
        assertEquals(teamMemberDto, result.get(0));
    }

    @Test
    public void testGetTeamMembersByFilterWithApplicableFilters() {
        long teamId = 1L;
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));

        when(teamMemberJpaRepository.findAll()).thenReturn(List.of(teamMember));
        when(teamMemberMapper.toTeamMemberDto(teamMember)).thenReturn(teamMemberDto);

        TeamFilterDto filter = new TeamFilterDto();
        filter.setTeamRole(TeamRole.DEVELOPER);

        List<TeamMemberDto> result = teamMemberService.getTeamMembersByFilter(teamId, filter);

        assertEquals(1, result.size());
        assertEquals(teamMemberDto, result.get(0));
    }

        @Test
    public void testGetAllTeamMembersShouldReturnAllMembers() {
        List<TeamMember> members = List.of(teamMember);
        Page<TeamMember> teamMembersPage = new PageImpl<>(List.of(teamMember));
        when(teamMemberJpaRepository.findAll(pageable)).thenReturn(teamMembersPage);
        when(teamMemberMapper.toTeamMemberDtos(members)).thenReturn(List.of(teamMemberDto));

        Page<TeamMemberDto> result = teamMemberService.getAllTeamMembers(pageable);

        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0)).isEqualTo(teamMemberDto);
    }

    @Test
    public void testGetTeamMemberByIdShouldReturnMemberWhenExists() {
        long teamMemberId = 1L;
        when(teamMemberMapper.toTeamMemberDto(any())).thenReturn(teamMemberDto);

        TeamMemberDto result = teamMemberService.getTeamMemberById(teamMemberId);

        assertThat(result).isEqualTo(teamMemberDto);
    }
}
