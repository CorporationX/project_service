package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.publisher.InviteSentEventPublisher;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.validator.TeamMemberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TeamMemberServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private TeamMemberMapper teamMemberMapper;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberValidator teamMemberValidator;

    @Mock
    private InviteSentEventPublisher inviteSentEventPublisher;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private TeamMemberDto teamMemberDto;
    private Team team;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teamMemberDto = TeamMemberDto.builder()
                .id(1L)
                .userId(2L)
                .nickname("Test User")
                .roles(Arrays.asList(TeamRole.DEVELOPER))
                .teamId(3L)
                .build();

        team = new Team();
        team.setId(3L);

        teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setUserId(2L);
        teamMember.setNickname("Test User");
        teamMember.setRoles(Arrays.asList(TeamRole.DEVELOPER));
        teamMember.setTeam(team);
    }

    @Test
    void testAddMember() {
        when(userServiceClient.getUser(any(Long.class))).thenReturn(null);
        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(team));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);
        when(teamMemberMapper.teamMemberDtoToTeamMember(any(TeamMemberDto.class))).thenReturn(teamMember);
        when(teamMemberMapper.teamMemberToTeamMemberDto(any(TeamMember.class))).thenReturn(teamMemberDto);

        TeamMemberDto result = teamMemberService.addMember(teamMemberDto, 1L, 2L);

        assertNotNull(result);
        assertEquals("Test User", result.getNickname());
        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
    }

    @Test
    void testAddMember_EntityNotFound() {
        when(userServiceClient.getUser(any(Long.class))).thenReturn(null);
        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            teamMemberService.addMember(teamMemberDto, 1L, 2L);
        });

        assertEquals("Команда с id 3 не найдена", exception.getMessage());
    }

    @Test
    void testUpdateMember() {
        when(userServiceClient.getUser(any(Long.class))).thenReturn(null);
        when(teamMemberRepository.findByUserIdAndProjectId(any(Long.class), any(Long.class))).thenReturn(teamMember);
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);
        when(teamMemberMapper.teamMemberToTeamMemberDto(any(TeamMember.class))).thenReturn(teamMemberDto);

        teamMemberDto.setNickname("Updated Nickname");

        TeamMemberDto result = teamMemberService.updateMember(teamMemberDto, 2L, 1L);

        assertNotNull(result);
        assertEquals("Updated Nickname", result.getNickname());
    }

    @Test
    void testRemoveMember_BusinessException() {
        when(teamMemberValidator.isMemberOwner(any(TeamMember.class))).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            teamMemberService.removeMember(1L, 2L, 1L);
        });

        assertEquals("Удалить пользователя может только владелец проекта", exception.getMessage());
    }

    @Test
    void testGetProjectMembers() {
        when(teamMemberRepository.findByProjectId(any(Long.class))).thenReturn(Arrays.asList(teamMember));
        when(teamMemberMapper.teamMemberListToTeamMemberDtoList(any(List.class))).thenReturn(Arrays.asList(teamMemberDto));

        List<TeamMemberDto> result = teamMemberService.getProjectMembers(1L, "DEVELOPER", null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
