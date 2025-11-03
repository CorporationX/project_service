package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.CreateStageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceImplTest {

    @Mock
    private StageInvitationRepository invitationRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Spy
    private StageInvitationMapper stageInvitationMapper;

    @InjectMocks
    private StageInvitationServiceImpl stageInvitationService;

    private Stage stage;
    private TeamMember author;
    private TeamMember invited;
    private Project project;
    private CreateStageInvitationDto createDto;
    private StageInvitation invitation;
    private StageInvitationDto invitationDto;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .build();

        stage = Stage.builder()
                .stageId(1L)
                .project(project)
                .executors(new ArrayList<>())
                .build();

        author = TeamMember.builder()
                .id(1L)
                .userId(100L)
                .build();

        invited = TeamMember.builder()
                .id(2L)
                .userId(200L)
                .build();

        createDto = CreateStageInvitationDto.builder()
                .stageId(1L)
                .authorUserId(100L)
                .invitedUserId(200L)
                .description("Please help with urgent stage")
                .build();

        invitation = StageInvitation.builder()
                .id(1L)
                .author(author)
                .invited(invited)
                .stage(stage)
                .description("Please help with urgent stage")
                .status(StageInvitationStatus.PENDING)
                .build();

        invitationDto = StageInvitationDto.builder()
                .id(1L)
                .stageId(1L)
                .authorUserId(100L)
                .invitedUserId(200L)
                .description("Please help with urgent stage")
                .status(StageInvitationStatus.PENDING)
                .build();
    }

    @Test
    void sendInvitationSuccess() {
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(teamMemberRepository.findByUserIdAndProjectId(100L, 1L)).thenReturn(author);
        when(teamMemberRepository.findByUserIdAndProjectId(200L, 1L)).thenReturn(invited);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(stageInvitationMapper.toInvitationDto(invitation)).thenReturn(invitationDto);

        StageInvitationDto result = stageInvitationService.sendInvitation(createDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(StageInvitationStatus.PENDING, result.status());

        verify(stageRepository).findById(1L);
        verify(teamMemberRepository).findByUserIdAndProjectId(100L, 1L);
        verify(teamMemberRepository).findByUserIdAndProjectId(200L, 1L);
        verify(invitationRepository).save(any(StageInvitation.class));
        verify(stageInvitationMapper).toInvitationDto(invitation);
    }

    @Test
    void sendInvitationStageNotFound() {
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.sendInvitation(createDto);
        });

        verify(stageRepository).findById(1L);
        verifyNoInteractions(teamMemberRepository, invitationRepository, stageInvitationMapper);
    }

    @Test
    void sendInvitationAuthorNotFound() {
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(teamMemberRepository.findByUserIdAndProjectId(100L, 1L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.sendInvitation(createDto);
        });

        verify(stageRepository).findById(1L);
        verify(teamMemberRepository).findByUserIdAndProjectId(100L, 1L);
        verifyNoMoreInteractions(teamMemberRepository);
    }

    @Test
    void sendInvitationInvitedUserNotFound() {
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(teamMemberRepository.findByUserIdAndProjectId(100L, 1L)).thenReturn(author);
        when(teamMemberRepository.findByUserIdAndProjectId(200L, 1L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.sendInvitation(createDto);
        });

        verify(stageRepository).findById(1L);
        verify(teamMemberRepository).findByUserIdAndProjectId(100L, 1L);
        verify(teamMemberRepository).findByUserIdAndProjectId(200L, 1L);
    }

    @Test
    void acceptInvitationSuccess() {
        StageInvitation acceptedInvitation = StageInvitation.builder()
                .id(1L)
                .author(author)
                .invited(invited)
                .stage(stage)
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        StageInvitationDto acceptedDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(acceptedInvitation);
        when(stageInvitationMapper.toInvitationDto(acceptedInvitation)).thenReturn(acceptedDto);

        StageInvitationDto result = stageInvitationService.acceptInvitation(1L);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, result.status());
        assertTrue(stage.getExecutors().contains(invited));

        verify(invitationRepository).findById(1L);
        verify(invitationRepository).save(invitation);
        verify(stageRepository).save(stage);
        verify(stageInvitationMapper).toInvitationDto(acceptedInvitation);
    }

    @Test
    void acceptInvitationInvitationNotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.acceptInvitation(1L);
        });

        verify(invitationRepository).findById(1L);
        verifyNoMoreInteractions(invitationRepository);
    }

    @Test
    void acceptInvitationAlreadyAccepted() {
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThrows(IllegalStateException.class, () -> {
            stageInvitationService.acceptInvitation(1L);
        });

        verify(invitationRepository).findById(1L);
        verifyNoMoreInteractions(invitationRepository);
    }

    @Test
    void acceptInvitationUserAlreadyExecutor() {
        stage.getExecutors().add(invited);
        StageInvitation acceptedInvitation = StageInvitation.builder()
                .id(1L)
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        StageInvitationDto acceptedDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(acceptedInvitation);
        when(stageInvitationMapper.toInvitationDto(acceptedInvitation)).thenReturn(acceptedDto);

        StageInvitationDto result = stageInvitationService.acceptInvitation(1L);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, result.status());
        assertEquals(1, stage.getExecutors().size());

        verify(invitationRepository).findById(1L);
        verify(invitationRepository).save(invitation);
        verify(stageRepository, never()).save(stage); // Stage not saved since user already executor
    }

    @Test
    void rejectInvitationSuccess() {
        String reason = "Too busy with other tasks";
        StageInvitation rejectedInvitation = StageInvitation.builder()
                .id(1L)
                .author(author)
                .invited(invited)
                .stage(stage)
                .status(StageInvitationStatus.REJECTED)
                .description(reason)
                .build();

        StageInvitationDto rejectedDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.REJECTED)
                .description(reason)
                .build();

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(rejectedInvitation);
        when(stageInvitationMapper.toInvitationDto(rejectedInvitation)).thenReturn(rejectedDto);

        StageInvitationDto result = stageInvitationService.rejectInvitation(1L, reason);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, result.status());
        assertEquals(reason, result.description());

        verify(invitationRepository).findById(1L);
        verify(invitationRepository).save(invitation);
        verify(stageInvitationMapper).toInvitationDto(rejectedInvitation);
    }

    @Test
    void rejectInvitationInvitationNotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.rejectInvitation(1L, "reason");
        });

        verify(invitationRepository).findById(1L);
        verifyNoMoreInteractions(invitationRepository);
    }

    @Test
    void rejectInvitationAlreadyRejected() {
        invitation.setStatus(StageInvitationStatus.REJECTED);
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThrows(IllegalStateException.class, () -> {
            stageInvitationService.rejectInvitation(1L, "reason");
        });

        verify(invitationRepository).findById(1L);
        verifyNoMoreInteractions(invitationRepository);
    }

    @Test
    void getInvitationsForUserWithStatusFilter() {
        Long userId = 200L;
        StageInvitationStatus status = StageInvitationStatus.PENDING;

        List<StageInvitation> invitations = List.of(invitation);
        List<StageInvitationDto> invitationDtos = List.of(invitationDto);

        when(invitationRepository.findAll()).thenReturn(invitations);
        when(stageInvitationMapper.toInvitationDto(invitation)).thenReturn(invitationDto);

        List<StageInvitationDto> result = stageInvitationService.getInvitationsForUser(userId, status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(invitationDto, result.get(0));

        verify(invitationRepository).findAll();
        verify(stageInvitationMapper).toInvitationDto(invitation);
    }

    @Test
    void getInvitationsForUserWithoutStatusFilter() {
        Long userId = 200L;

        List<StageInvitation> invitations = List.of(invitation);
        List<StageInvitationDto> invitationDtos = List.of(invitationDto);

        when(invitationRepository.findAll()).thenReturn(invitations);
        when(stageInvitationMapper.toInvitationDto(invitation)).thenReturn(invitationDto);

        List<StageInvitationDto> result = stageInvitationService.getInvitationsForUser(userId, null);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(invitationRepository).findAll();
        verify(stageInvitationMapper).toInvitationDto(invitation);
    }

    @Test
    void getInvitationsForUserNoInvitationsFound() {
        Long userId = 300L;
        when(invitationRepository.findAll()).thenReturn(List.of(invitation));

        List<StageInvitationDto> result = stageInvitationService.getInvitationsForUser(userId, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(invitationRepository).findAll();
        verifyNoInteractions(stageInvitationMapper);
    }

    @Test
    void getInvitationsForUserFilteredByDifferentStatus() {
        Long userId = 200L;
        StageInvitationStatus status = StageInvitationStatus.ACCEPTED;

        List<StageInvitation> invitations = List.of(invitation);

        when(invitationRepository.findAll()).thenReturn(invitations);

        List<StageInvitationDto> result = stageInvitationService.getInvitationsForUser(userId, status);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(invitationRepository).findAll();
        verifyNoInteractions(stageInvitationMapper);
    }
}