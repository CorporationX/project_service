package faang.school.projectservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidateInviteException;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StageInvitationServiceTest {
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageRepository stageRepository;
    @InjectMocks
    private StageInvitationService stageInvitationService;
    TeamMember invitedUser;
    TeamMember author;
    StageInvitationDto stageInvitationDto;

    Stage stage;
    StageInvitation stageInvitation;

    @BeforeEach
    void setUp() {
        Long invitedUserId = 1L;
        Long authorId = 2L;

        invitedUser = new TeamMember();
        invitedUser.setId(111L);
        invitedUser.setUserId(invitedUserId);

        author = new TeamMember();
        author.setId(112L);
        author.setUserId(authorId);

        stage = new Stage();
        stage.setStageId(20L);

        stageInvitationDto = new StageInvitationDto();
        stageInvitationDto.setId(123L);
        stageInvitationDto.setInvited(invitedUser);
        stageInvitationDto.setStage(stage);

        stageInvitation = new StageInvitation();
        stageInvitation.setId(123L);
        stageInvitation.setStage(stage);

        when(teamMemberRepository.findById(invitedUserId)).thenReturn(invitedUser);
        when(teamMemberRepository.findById(authorId)).thenReturn(invitedUser);

        when(stageRepository.getById(20L)).thenReturn(stage);

        when(stageInvitationRepository.findById(123L)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toEntity(stageInvitationDto)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
    }

    @Test
    public void testSendInvitation_Success() {
        invitedUser.setRoles(List.of(TeamRole.DEVELOPER));
        author.setRoles(List.of(TeamRole.OWNER));
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        List<TeamMember> executors = new ArrayList<>();
        executors.add(author);
        stage.setExecutors(executors);

        StageInvitationDto result = stageInvitationService.sendInvitation(stageInvitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.PENDING, result.getStatus());
        assertEquals(author, result.getAuthor());
        assertEquals(invitedUser, result.getInvited());

        verify(teamMemberRepository, times(1)).findById(invitedUser.getUserId());
        verify(stageInvitationRepository, times(1)).save(any(StageInvitation.class));
        verify(stageInvitationMapper, never()).listToEntity(anyList());
    }

    @Test
    public void testSendInvitation_InviteUserWithOwnerRole() {
        invitedUser.setRoles(List.of(TeamRole.OWNER));

        DataValidateInviteException exception = assertThrows(DataValidateInviteException.class, () -> {
            stageInvitationService.sendInvitation(stageInvitationDto);
        });

        String expectedMessage = "Can't send invitation to user with OWNER or MANAGER role";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testSendInvitation_InviteUserWithManagerRole() {
        invitedUser.setRoles(List.of(TeamRole.MANAGER));

        DataValidateInviteException exception = assertThrows(DataValidateInviteException.class, () -> {
            stageInvitationService.sendInvitation(stageInvitationDto);
        });

        String expectedMessage = "Can't send invitation to user with OWNER or MANAGER role";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testSendInvitation_InvitationSuccessfullySent() {
        invitedUser.setRoles(List.of(TeamRole.DEVELOPER));
        author.setRoles(List.of(TeamRole.OWNER));

        List<TeamMember> executors = new ArrayList<>();
        executors.add(author);
        stage.setExecutors(executors);

        when(stageInvitationMapper.toEntity(stageInvitationDto)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.sendInvitation(stageInvitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.PENDING, result.getStatus());
        assertEquals(author, result.getAuthor());
        assertEquals(invitedUser, result.getInvited());

        verify(teamMemberRepository, times(1)).findById(invitedUser.getUserId());
        verify(stageInvitationRepository, times(1)).save(any(StageInvitation.class));
        verify(stageInvitationMapper, never()).listToEntity(anyList());
    }

    @Test
    public void testSendInvitation_UserIsAlreadyExecutorThisStage() {
        invitedUser.setRoles(List.of(TeamRole.DEVELOPER));
        author.setRoles(List.of(TeamRole.OWNER));

        List<TeamMember> executors = new ArrayList<>();
        executors.add(author);
        executors.add(invitedUser);
        stage.setExecutors(executors);

        DataValidateInviteException exception = assertThrows(DataValidateInviteException.class, () -> {
            stageInvitationService.sendInvitation(stageInvitationDto);
        });

        String expectedMessage = "User is already executor for this stage";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testAcceptInvitation_Success() {
        stage.setExecutors(new ArrayList<>());
        stageInvitation.setInvited(invitedUser);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        StageInvitationDto result = stageInvitationService.acceptInvitation(stageInvitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, result.getStatus());
        assertEquals(invitedUser, result.getInvited());
        assertEquals(1, result.getStage().getExecutors().size());
        assertTrue(result.getStage().getExecutors().contains(invitedUser));
    }

    @Test
    public void testAcceptInvitation_UserIsAlreadyExecutor() {
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        List<TeamMember> executors = new ArrayList<>();
        executors.add(invitedUser);
        stage.setExecutors(executors);

        assertThrows(DataValidateInviteException.class, () -> stageInvitationService.acceptInvitation(stageInvitationDto));
    }

    @Test
    public void testAcceptInvitation_InvalidInvitationId() {
        when(stageInvitationRepository.findById(123L)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> stageInvitationService.acceptInvitation(stageInvitationDto));
    }

    @Test
    public void testAcceptInvitation_NullInput() {
        assertThrows(NullPointerException.class, () -> stageInvitationService.acceptInvitation(null));
    }

    @Test
    public void testRejectInvitation_Success() {
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitationDto result = stageInvitationService.rejectInvitation(stageInvitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, result.getStatus());
    }
    @Test
    public void testRejectInvitation_InvalidStatus() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationRepository.findById(123L)).thenReturn(stageInvitation);

        assertThrows(DataValidateInviteException.class, () -> stageInvitationService.rejectInvitation(stageInvitationDto));
    }

    @Test
    public void testRejectInvitation_InvalidId() {
        when(stageInvitationRepository.findById(123L)).thenReturn(null);

        assertThrows(DataValidateInviteException.class, () -> stageInvitationService.rejectInvitation(stageInvitationDto));
    }
}