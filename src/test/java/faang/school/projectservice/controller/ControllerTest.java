package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @InjectMocks
    private StageInvitationController stageInvitationController;

    @Mock
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    private StageInvitationDto stageInvitationDto;
    private InvitationFilterDto invitationFilterDto;
    private Long userId;

    @BeforeEach
    public void setUp() {
        stageInvitationDto = StageInvitationDto.builder().id(1L).stageId(1L).invitedId(1L).build();
        invitationFilterDto = InvitationFilterDto.builder().stageId(1L).authorId(1L).status(StageInvitationStatus.PENDING).build();
        userId = 1L;
    }

    @Test
    public void testCorrectWorkCreateInvitation() {
        assertDoesNotThrow(() -> stageInvitationController.createInvitation(stageInvitationDto));
    }

    @Test
    public void testInCorrectWorkCreateInvitation() {
        doThrow(DataValidationException.class).when(stageInvitationValidator).createValidationController(stageInvitationDto);
        assertThrows(DataValidationException.class, () -> stageInvitationController.createInvitation(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkAcceptInvitation() {
        assertDoesNotThrow(() -> stageInvitationController.acceptInvitation(stageInvitationDto));
    }

    @Test
    public void testInCorrectWorkAcceptInvitation() {
        doThrow(DataValidationException.class).when(stageInvitationValidator).acceptInvitationValidationController(stageInvitationDto);
        assertThrows(DataValidationException.class, () -> stageInvitationController.acceptInvitation(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkRejectInvitation() {
        assertDoesNotThrow(() -> stageInvitationController.rejectInvitation(stageInvitationDto));
    }

    @Test
    public void testInCorrectWorkRejectInvitation() {
        doThrow(DataValidationException.class).when(stageInvitationValidator).rejectInvitationValidationController(stageInvitationDto);
        assertThrows(DataValidationException.class, () -> stageInvitationController.rejectInvitation(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkShowAllInvitationForMember() {
        assertDoesNotThrow(() -> stageInvitationController.showAllInvitationForMember(userId, invitationFilterDto));
    }

    @Test
    public void testInCorrectWorkShowAllInvitationForMember() {
        doThrow(DataValidationException.class).when(stageInvitationValidator).showAllInvitationForMemberValidationController(userId, invitationFilterDto);
        assertThrows(DataValidationException.class, () -> stageInvitationController.showAllInvitationForMember(userId, invitationFilterDto));
    }
}
