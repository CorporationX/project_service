package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import faang.school.projectservice.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.validator.stage_invitation.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageInvitationServiceTest {
    @Mock
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @Mock
    private List<StageInvitationFilter> stageInvitationFilters;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @InjectMocks
    StageInvitationService stageInvitationService;

    private Stage stage;
    private StageDto stageDto;
    private StageInvitationFilterDto filter;
    private StageInvitation stageInvitation;
    private StageInvitationDto stageInvitationDto;
    private List<StageInvitation> stageInvitations;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        stageInvitationDto = StageInvitationDto.builder().
                id(1L).
                authorId(2L).
                invitedId(3L).build();
        stageInvitation = StageInvitation.builder().id(1L).build();

        stageInvitations = Arrays.asList(stageInvitation);

        stageDto = StageDto.builder().stageId(1L).executorsId(Arrays.asList(5L)).build();
    }

    @Test
    void testSendAnInvitation_success() {
        StageInvitation stageInvitation = new StageInvitation();
        StageInvitationDto expectedDto = StageInvitationDto
                .builder()
                .status(PENDING)
                .build();

        doNothing().when(stageInvitationValidator).checkWhetherThisRequestExists(1L);
        doNothing().when(stageInvitationValidator).checkTheExistenceOfTheInvitee(3L);
        when(stageInvitationMapper.toEntity(stageInvitationDto)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationRepository).save(stageInvitation);

        StageInvitationDto result = stageInvitationService.sendAnInvitation(stageInvitationDto);

        verify(stageInvitationValidator).checkWhetherThisRequestExists(1L);
        verify(stageInvitationValidator).checkTheExistenceOfTheInvitee(3L);
        verify(stageInvitationMapper).toEntity(stageInvitationDto);
        verify(stageInvitationRepository).save(stageInvitation);

        assertNotNull(result);
        assertEquals(PENDING, result.getStatus());
    }

    @Test
    void testSendAnInvitation_WhenRequestAlreadyExists_throwsException() {
        doThrow(new DataValidationException("This invitation already exists"))
                .when(stageInvitationValidator).checkWhetherThisRequestExists(1L);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> stageInvitationService.sendAnInvitation(stageInvitationDto)
        );

        assertEquals("This invitation already exists", dataValidationException.getMessage());

        verify(stageInvitationValidator).checkWhetherThisRequestExists(1L);
        verify(stageInvitationValidator, never()).checkTheExistenceOfTheInvitee(any());
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    void testSendAnInvitation_WhenInviteeDoesNotExist_throwsException() {
        doNothing().when(stageInvitationValidator).checkWhetherThisRequestExists(1L);
        doThrow(new DataValidationException("This team member does not exist"))
                .when(stageInvitationValidator).checkTheExistenceOfTheInvitee(3L);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> stageInvitationService.sendAnInvitation(stageInvitationDto)
        );

        assertEquals("This team member does not exist", dataValidationException.getMessage());
        verify(stageInvitationValidator).checkWhetherThisRequestExists(1L);
        verify(stageInvitationValidator).checkTheExistenceOfTheInvitee(3L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    void testAcceptAnInvitation_noStageDtoFound() {
        when(stageService.getById(1L)).thenReturn(null);

        StageInvitationDto result = stageInvitationService.acceptAnInvitation(stageInvitationDto);

        assertNull(result.getId(), "Result should be null because stageDto is not found");
    }

    @Test
    void testRejectAnInvitation_success() {
        String rejectionReason = "The reason for the refusal must be indicated";
        stageInvitationDto.setRejection(rejectionReason);

        StageInvitationDto result = stageInvitationService.rejectAnInvitation(stageInvitationDto);

        verify(stageInvitationValidator).checkTheReasonForTheFailure(rejectionReason);
        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, result.getStatus());
    }

    @Test
    void testRejectAnInvitation_validatorThrowsException() {
        String rejectionReason = "The reason for the refusal must be indicated";
        stageInvitationDto.setRejection(rejectionReason);

        doThrow(new DataValidationException("The reason for the refusal must be indicated")).
                when(stageInvitationValidator).checkTheReasonForTheFailure(rejectionReason);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            stageInvitationService.rejectAnInvitation(stageInvitationDto);
        });

        assertEquals("The reason for the refusal must be indicated", dataValidationException.getMessage());
    }

    @Test
    void testViewAllInvitationsForOneParticipant_withoutFilters() {
        when(stageInvitationRepository.findByInvitedUserId(1L)).thenReturn(stageInvitations);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationFilterDto filterDto = null;

        List<StageInvitationDto> result = stageInvitationService.viewAllInvitationsForOneParticipant(1L, filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stageInvitationDto, result.get(0));

        verify(stageInvitationRepository).findByInvitedUserId(1L);
        verify(stageInvitationMapper).toDto(stageInvitation);
    }
}