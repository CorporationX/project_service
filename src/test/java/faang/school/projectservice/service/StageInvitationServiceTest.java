package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;
    @InjectMocks
    StageInvitationService stageInvitationService;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
    private Long invitationId;
    private StageInvitation stageInvitation;
    private TeamMember invited;
    private TeamMember author;
    private Stage stage;

    @BeforeEach
    public void initialize() {
        stageId = 100L;
        authorId = 200L;
        invitedId = 300L;
        invitationId = 400L;
        author = new TeamMember();
        author.setId(authorId);
        invited = new TeamMember();
        invited.setId(invitedId);
        stage = new Stage();
        stage.setStageId(stageId);
        stageInvitation = StageInvitation.builder()
                .invited(invited)
                .author(author)
                .stage(stage)
                .id(invitationId)
                .description("smth")
                .build();
    }

    @Test
    public void sendInvitationTest() {
        Mockito.when(stageRepository.getById(stageId)).thenReturn(stage);
        Mockito.when(teamMemberRepository.findById(authorId)).thenReturn(author);
        Mockito.when(teamMemberRepository.findById(invitedId)).thenReturn(invited);
        stageInvitationService.sendInvitation(stageInvitationMapper.toDto(stageInvitation));
        ArgumentCaptor<StageInvitation> argumentCaptor = ArgumentCaptor.forClass(StageInvitation.class);
        verify(stageInvitationRepository, times(1)).save(argumentCaptor.capture());
        verify(stageInvitationMapper, times(1)).toDto(stageInvitationRepository.save(argumentCaptor.capture()));
    }

    @Test
    public void acceptInvitationTestThrowsException() {
        Mockito.when(stageInvitationRepository.findById(invitationId)).thenReturn(stageInvitation);
        DataValidationException exception = Assertions
                .assertThrows(DataValidationException.class, () -> stageInvitationService
                        .acceptInvitation(invitationId, authorId));
        assertTrue(exception.getMessage().contains("can not"));
    }

    @Test
    public void acceptInvitationTest() {
        Mockito.when(stageInvitationRepository.findById(invitationId)).thenReturn(stageInvitation);
        Mockito.when(teamMemberRepository.findById(invitedId)).thenReturn(invited);
        stageInvitationService.acceptInvitation(invitationId, invitedId);
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
    }

    @Test
    public void declineInvitationThrowsExceptionWhenDeclinedWithNotInvitedTest() {
        Mockito.when(stageInvitationRepository.findById(invitationId)).thenReturn(stageInvitation);
        DataValidationException exception = Assertions
                .assertThrows(DataValidationException.class, () -> stageInvitationService
                        .declineInvitation(invitationId, authorId, "some reason"));
        assertTrue(exception.getMessage().contains("not invited"));
    }

    @Test
    public void declineInvitationTest() {
        Mockito.when(stageInvitationRepository.findById(invitationId)).thenReturn(stageInvitation);
        stageInvitationService.declineInvitation(invitationId, invitedId, "addf");
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
    }

    @Test
    public void getAllInvitationsForUserWithStatusTest() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        Mockito.when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));
        Mockito.when(teamMemberRepository.findById(invitedId)).thenReturn(invited);
        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForUserWithStatus(invitedId, StageInvitationStatus.REJECTED);
        assertSame(result.get(0).getId(), invitationId);
    }

}
