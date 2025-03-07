package faang.school.projectservice.unit.stage.service;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.filterDto.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.invitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.validator.StageInvitationValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberService teamMemberService;

    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Spy
    @InjectMocks
    private StageInvitationService spyStageInvitationService;


    private StageInvitationService stageInvitationService;

    @BeforeEach
    void init() {
        StageInvitationFilter filter = Mockito.mock(StageInvitationFilter.class);
        List<StageInvitationFilter> filters = List.of(filter);
        stageInvitationService = new StageInvitationService(stageInvitationRepository,
                stageService, teamMemberService, stageInvitationMapper, stageInvitationValidator, filters);
    }

    @Captor
    private ArgumentCaptor<StageInvitation> stageInvitationCaptor;

    @Test
    public void sendInvitation_whereStageNotFound() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(2L);
        dto.setStageId(1L);
        when(stageService.getStageById(dto.getStageId()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> stageInvitationService.sendInvitation(dto));

    }

    @Test
    public void sendInvitation_whereTeamMemberNotFound() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(2L);
        dto.setStageId(1L);
        when(stageService.getStageById(dto.getStageId())).thenReturn(new Stage());
        when(teamMemberService.getTeamMemberById(dto.getInvitedId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> stageInvitationService.sendInvitation(dto));
    }

    @Test
    public void sendInvitation() {

        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(2L);
        dto.setStageId(1L);
        Stage stage = new Stage();
        stage.setStageId(dto.getStageId());
        TeamMember author = new TeamMember();
        author.setId(dto.getAuthorId());
        TeamMember invited = new TeamMember();
        invited.setId(dto.getInvitedId());
        when(stageService.getStageById(dto.getStageId())).thenReturn(stage);
        when(teamMemberService.getTeamMemberById(dto.getInvitedId())).thenReturn(invited);
        doReturn(dto).when(spyStageInvitationService).createStageInvitationAndGetDto(stage, dto.getAuthorId(), invited);

        StageInvitationDto sentInvitation = spyStageInvitationService.sendInvitation(dto);

        assertEquals(dto, sentInvitation);
        verify(stageService, times(1)).getStageById(dto.getStageId());
        verify(teamMemberService, times(1)).getTeamMemberById(dto.getInvitedId());
        verify(spyStageInvitationService, times(1))
                .createStageInvitationAndGetDto(stage, dto.getAuthorId(), invited);
    }

    @Test
    public void createStageInvitationAndGetDto() {
        long stageId = 1L;
        long authorId = 1L;
        long invitedId = 2L;
        Stage stage = new Stage();
        stage.setStageId(stageId);
        TeamMember author = new TeamMember();
        author.setId(authorId);
        TeamMember invited = new TeamMember();
        invited.setId(invitedId);
        StageInvitation invitation = new StageInvitation();
        invitation.setStage(stage);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        invitation.setStatus(StageInvitationStatus.PENDING);
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(author);
        doNothing().when(stageInvitationValidator).validateInvitedForCreate(authorId, invitedId);
        StageInvitationDto expectedStageInvitationDto = stageInvitationService
                .createStageInvitationAndGetDto(stage, authorId, invited);

        assertEquals(expectedStageInvitationDto, stageInvitationMapper.toDto(stageInvitationCaptor.capture()));
        verify(teamMemberService, times(1)).getTeamMemberById(authorId);
        verify(stageInvitationValidator, times(1)).validateInvitedForCreate(authorId, invitedId);
        verify(stageInvitationRepository, times(1)).save(stageInvitationCaptor.capture());

    }

    @Test
    public void acceptInvitation_whenInvitationAccept() {
        Long invitationId = 1L;
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> stageInvitationService.acceptStageInvitation(invitationId));
        verify(stageInvitationRepository, never()).save(any(StageInvitation.class));
    }

    @Test
    public void acceptInvitation_whenInvitationRejected() {
        Long invitationId = 1L;
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> stageInvitationService.acceptStageInvitation(invitationId));
        verify(stageInvitationRepository, never()).save(any(StageInvitation.class));
    }

    @Test
    public void acceptInvitation() {
        Long invitationId = 1L;
        final Stage stage = new Stage();
        TeamMember invited = new TeamMember();
        invited.setStages(new ArrayList<>());

        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.PENDING);
        invitation.setInvited(invited);
        invitation.setStage(stage);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doNothing().when(stageInvitationValidator).validateStatusPendingCheck(stageInvitationCaptor.capture());

        final StageInvitationDto expectedAccept = stageInvitationService.acceptStageInvitation(invitationId);

        verify(stageInvitationRepository, times(1)).getReferenceById(invitationId);
        verify(stageInvitationValidator, times(1)).validateStatusPendingCheck(invitation);
        verify(stageInvitationRepository, times(1)).save(stageInvitationCaptor.capture());
        assertEquals(expectedAccept, stageInvitationMapper.toDto(stageInvitationCaptor.capture()));

    }

    @Test
    public void rejectStageInvitation_withBlankRejectionReason() {
        Long invitationId = 1L;
        String rejectionReason = " ";

        DataValidationException result = assertThrows(DataValidationException.class,
                () -> stageInvitationService.rejectStageInvitation(invitationId, rejectionReason));

        assertEquals("There must be a reason for rejecting an invitation.", result.getMessage());
        verify(stageInvitationRepository, never()).save(any(StageInvitation.class));
    }


    @Test
    public void rejectStageInvitation_whenInvitationAccept() {
        Long invitationId = 1L;
        String text = "text";
        StageInvitation invitation = new StageInvitation();
        invitation.setRejectionReason(text);
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> {
            stageInvitationService.rejectStageInvitation(invitation.getId(), invitation.getRejectionReason());
        });
    }

    @Test
    public void rejectStageInvitation_whenInvitationRejected() {
        Long invitationId = 1L;
        String text = "text";
        StageInvitation invitation = new StageInvitation();
        invitation.setRejectionReason(text);
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> {
            stageInvitationService.rejectStageInvitation(invitation.getId(), invitation.getRejectionReason());
        });
    }

    @Test
    public void rejectStageInvitation() {
        Long invitationId = 1L;
        final String text = "text";
        final Stage stage = new Stage();
        TeamMember invited = new TeamMember();
        invited.setStages(new ArrayList<>());
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setInvited(invited);
        invitation.setRejectionReason(text);
        invitation.setId(invitationId);
        invitation.setStage(stage);
        invitation.setStatus(StageInvitationStatus.PENDING);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);

        StageInvitationDto expectedStageInvitationDto = stageInvitationService
                .rejectStageInvitation(invitationId, text);

        verify(stageInvitationRepository, times(1)).save(stageInvitationCaptor.capture());
        assertEquals(expectedStageInvitationDto, stageInvitationMapper.toDto(stageInvitationCaptor.capture()));
    }

    @Test
    public void viewAllInvitation() {
        Long userId = 1L;
        StageInvitationFilterDto filter = new StageInvitationFilterDto();

        stageInvitationService.viewAllInvitation(userId, filter);

        verify(stageInvitationRepository, times(1)).findAll();
    }


}
