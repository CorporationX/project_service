package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.teamMember.CreateTeamMemberDto;
import faang.school.projectservice.dto.teamMember.ResponseTeamMemberDto;
import faang.school.projectservice.dto.teamMember.UpdateTeamMemberDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.team.Team;
import faang.school.projectservice.model.team.TeamMember;
import faang.school.projectservice.model.team.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberMapper teamMemberMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private CreateTeamMemberDto createDto;
    private UpdateTeamMemberDto updateDto;
    private TeamMember teamMember;
    private TeamMember member1;
    private TeamMember member2;
    private Team team;
    private Team team1;
    private Team team2;
    private Project project;
    private ResponseTeamMemberDto responseDto;

    private long teamId;
    private long creatorId;
    private long updaterId;
    private long memberId;
    private long deleterId;

    @BeforeEach
    public void setUp() {
        createDto = CreateTeamMemberDto.builder()
                .userId(2L)
                .roles(List.of(TeamRole.OWNER))
                .stageIds(List.of(1L))
                .build();

        updateDto = UpdateTeamMemberDto.builder()
                .stageIds(List.of(1L))
                .build();

        teamMember = TeamMember.builder()
                .userId(2L)
                .roles(List.of(TeamRole.OWNER))
                .build();

        team = new Team();
        team.setId(1L);
        team.setTeamMembers(List.of(TeamMember.builder().id(2L).build()));

        project = new Project();
        project.setId(1L);
        project.setName("Project A");
        project.setTeams(List.of(team));

        team.setProject(project);

        responseDto = ResponseTeamMemberDto.builder()
                .id(1L)
                .userId(2L)
                .roles(List.of(TeamRole.OWNER))
                .stageIds(List.of(1L))
                .build();

        teamId = 1L;
        creatorId = 1L;
        updaterId = 1L;
        deleterId = 1L;
        memberId = 2L;

        member1 = TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        member2 = TeamMember.builder()
                .id(2L)
                .roles(List.of(TeamRole.ANALYST))
                .build();

        team1 = new Team();
        team1.setId(1L);
        team1.setTeamMembers(List.of(member1));

        team2 = new Team();
        team2.setId(2L);
        team2.setTeamMembers(List.of(member2));
    }

    @Test
    public void testHasCuratorAccess_Curator() {
        // Arrange
        when(teamMemberRepository.findById(1L)).thenReturn(teamMember);

        // Act
        boolean result = teamMemberService.curatorHasNoAccess(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testHasCuratorAccess_NotCurator() {
        // Arrange
        teamMember.setRoles(List.of(TeamRole.ANALYST));
        when(teamMemberRepository.findById(2L)).thenReturn(teamMember);

        // Act
        boolean result = teamMemberService.curatorHasNoAccess(2L);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testAddTeamMember() {
        // Arrange
        when(userContext.getUserId()).thenReturn(creatorId);
        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(teamMemberMapper.toEntity(createDto)).thenReturn(teamMember);
        when(teamMemberMapper.toResponseDto(any(TeamMember.class))).thenReturn(responseDto);
        when(teamMemberRepository.findByUserIdAndProjectId(creatorId, project.getId()))
                .thenReturn(Optional.ofNullable(teamMember));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        // Act
        ResponseTeamMemberDto result = teamMemberService.addTeamMember(createDto, teamId);

        // Assert
        assertEquals(responseDto, result);
        verify(teamService).getTeamById(teamId);
        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    public void testUpdateTeamMember() {
        // Arrange
        when(userContext.getUserId()).thenReturn(updaterId);
        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(teamMemberRepository.findById(memberId)).thenReturn(teamMember);
        when(teamMemberRepository.findByUserIdAndProjectId(creatorId, project.getId()))
                .thenReturn(Optional.ofNullable(teamMember));
        when(teamMemberMapper.toResponseDto(teamMember)).thenReturn(responseDto);

        // Act
        ResponseTeamMemberDto result = teamMemberService.updateTeamMember(updateDto, teamId, memberId);

        // Assert
        assertEquals(updateDto.stageIds(), result.stageIds());
        verify(teamService).getTeamById(teamId);
        verify(teamMemberRepository).findById(memberId);
        verify(teamMemberRepository).save(teamMember);
    }
    @Test
    public void testDeleteTeamMember() {
        // Arrange
        when(userContext.getUserId()).thenReturn(deleterId);
        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(teamMemberRepository.findByUserIdAndProjectId(creatorId, project.getId()))
                .thenReturn(Optional.ofNullable(teamMember));
        when(teamMemberRepository.findById(memberId)).thenReturn(teamMember);

        // Act
        teamMemberService.deleteTeamMember(memberId, teamId);

        // Assert
        verify(teamService).getTeamById(teamId);
        verify(teamMemberRepository).delete(teamMember);
    }

    @Test
    public void testGetFilteredTeamMembers() {
        // Arrange
        String name = "John";
        TeamRole role = TeamRole.DEVELOPER;
        long projectId = 1L;

        project.setTeams(List.of(team1, team2));

        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setUsername("John Doe");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("Jane Doe");

        when(userServiceClient.getUsersByIds(List.of(1L, 2L))).thenReturn(List.of(user1, user2));
        when(teamMemberMapper.toResponseDto(anyList())).thenReturn(List.of(responseDto));
        when(teamMemberRepository.findAllMembersByProjectId(projectId)).thenReturn(List.of(member1, member2));

        // Act
        List<ResponseTeamMemberDto> result = teamMemberService.getFilteredTeamMembers(name, role, projectId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(userServiceClient).getUsersByIds(List.of(1L, 2L));
        verify(teamMemberMapper).toResponseDto(anyList());
        verify(teamMemberRepository).findAllMembersByProjectId(projectId);
    }

    @Test
    public void testGetTeamMembersByTeamId() {
        // Arrange
        team.setTeamMembers(List.of(member1, member2));

        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(teamMemberMapper.toResponseDto(List.of(member1, member2))).thenReturn(List.of(responseDto, responseDto));

        // Act
        List<ResponseTeamMemberDto> result = teamMemberService.getTeamMembersByTeamId(teamId);

        // Assert
        assertEquals(2, result.size());
        verify(teamService).getTeamById(teamId);
        verify(teamMemberMapper).toResponseDto(List.of(member1, member2));
    }

    @Test
    public void testGetTeamMemberValidId() {
        // arrange
        long id = 5L;
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findById(id)).thenReturn(teamMember);

        // act
        TeamMember returnedTeamMember = teamMemberService.getTeamMember(id);

        // assert
        assertEquals(teamMember, returnedTeamMember);
    }
}