package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.filter.InvitationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StageInvitationServiceImplTest {

    @InjectMocks
    private StageInvitationServiceImpl stageInvitationService;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private StageInvitationMapper mapper;

    private StageInvitation stageInvitation;
    @Mock
    private InvitationFilter invitationFilter1;
    @Mock
    private InvitationFilter invitationFilter2;

    private List<StageInvitation> invitations;
    private List<InvitationFilter> invitationFilters;

    private static final Long INVITATION_ID = 1L;
    private static final String REJECTION_REASON = "no time";


    @BeforeEach
    void setUp() {
        stageInvitation = new StageInvitation();
        stageInvitation.setId(INVITATION_ID);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        invitations = List.of(new StageInvitation(), new StageInvitation());
        invitationFilters = List.of(invitationFilter1, invitationFilter2);

        stageInvitationService = new StageInvitationServiceImpl(stageInvitationRepository, mapper, invitationFilters);
    }


    @Test
    void sendInvitation_ShouldThrowException_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> stageInvitationService.sendInvitation(null));
    }

    @Test
    void sendInvitation_ShouldMapAndSave_WhenDtoIsNotNull() {
        StageInvitationDto stageInvitationDto = new StageInvitationDto();
        stageInvitationDto.setId(1L);
        when(mapper.toEntity(stageInvitationDto)).thenReturn(new StageInvitation());

        stageInvitationService.sendInvitation(stageInvitationDto);

        verify(stageInvitationRepository, times(1)).save(any(StageInvitation.class));
    }

    @Test
    void acceptInvitation_ShouldThrowNotFoundException_WhenInvitationNotFound() {
        when(stageInvitationRepository.findById(INVITATION_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stageInvitationService.acceptInvitation(INVITATION_ID));
    }

    @Test
    void acceptInvitation_ShouldUpdateStatusAndSave_WhenStatusIsPending() {
        when(stageInvitationRepository.findById(INVITATION_ID)).thenReturn(stageInvitation);

        stageInvitationService.acceptInvitation(INVITATION_ID);

        assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
    }

    @Test
    void acceptInvitation_ShouldThrowIllegalStateException_WhenStatusIsNotPending() {
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(stageInvitationRepository.findById(INVITATION_ID)).thenReturn(stageInvitation);

        assertThrows(IllegalStateException.class, () -> stageInvitationService.acceptInvitation(INVITATION_ID));
        verify(stageInvitationRepository, never()).save(stageInvitation);
    }

    @Test
    void rejectInvitationWithReason_ShouldThrowNotFoundException_WhenInvitationNotFound() {
        when(stageInvitationRepository.findById(INVITATION_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stageInvitationService.acceptInvitation(INVITATION_ID));
    }

    @Test
    void rejectInvitationWithReason_ShouldUpdateStatusAndSave_WhenStatusIsPending() {
        when(stageInvitationRepository.findById(INVITATION_ID)).thenReturn(stageInvitation);

        stageInvitationService.rejectInvitationWithReason(INVITATION_ID, REJECTION_REASON);

        assertEquals(StageInvitationStatus.REJECTED, stageInvitation.getStatus());
        assertEquals(REJECTION_REASON, stageInvitation.getDescription());
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
    }

    @Test
    void rejectInvitationWithReason_ShouldThrowIllegalStateException_WhenStatusIsNotPending() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationRepository.findById(INVITATION_ID)).thenReturn(stageInvitation);

        assertThrows(IllegalStateException.class,
                () -> stageInvitationService.rejectInvitationWithReason(INVITATION_ID, REJECTION_REASON));
        verify(stageInvitationRepository, never()).save(stageInvitation);
    }

    @Test
    void getInvitationsWithFilters_ShouldReturnEmptyList_WhenInvitationsListIsEmpty() {
        StageInvitationFilterDTO filterDTO = new StageInvitationFilterDTO();

        when(stageInvitationRepository.findAll()).thenReturn(Collections.emptyList());

        List<StageInvitationDto> result = stageInvitationService.getInvitationsWithFilters(filterDTO);

        assertTrue(result.isEmpty());
        verify(stageInvitationRepository, times(1)).findAll();
    }

    @Test
    void getInvitationsWithFilters_ShouldApplyFiltersCorrectly() {
        StageInvitationFilterDTO filterDTO = new StageInvitationFilterDTO();

        when(stageInvitationRepository.findAll()).thenReturn(invitations);
        when(invitationFilter1.isApplicable(filterDTO)).thenReturn(true);
        when(invitationFilter2.isApplicable(filterDTO)).thenReturn(false);
        when(invitationFilter1.filter(any(Stream.class), eq(filterDTO))).thenReturn(invitations.stream().filter(inv -> true));
        when(mapper.toDto(any(StageInvitation.class))).thenReturn(new StageInvitationDto());

        List<StageInvitationDto> result = stageInvitationService.getInvitationsWithFilters(filterDTO);

        verify(invitationFilter1).filter(any(Stream.class), eq(filterDTO));
        verify(invitationFilter2, never()).filter(any(Stream.class), eq(filterDTO));
        assertEquals(2, result.size());
    }

    @Test
    void getInvitationsWithFilters_ShouldReturnAllInvitations_WhenNoFiltersAreApplicable() {
        StageInvitationFilterDTO filterDTO = new StageInvitationFilterDTO();

        when(stageInvitationRepository.findAll()).thenReturn(invitations);
        when(invitationFilter1.isApplicable(filterDTO)).thenReturn(false);
        when(invitationFilter2.isApplicable(filterDTO)).thenReturn(false);
        when(mapper.toDto(any(StageInvitation.class))).thenReturn(new StageInvitationDto());

        List<StageInvitationDto> result = stageInvitationService.getInvitationsWithFilters(filterDTO);

        verify(invitationFilter1, never()).filter(any(Stream.class), eq(filterDTO));
        verify(invitationFilter2, never()).filter(any(Stream.class), eq(filterDTO));
        assertEquals(2, result.size());
    }


}