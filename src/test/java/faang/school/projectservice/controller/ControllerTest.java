package faang.school.projectservice.controller;

import faang.school.projectservice.controller.stageInvitation.StageInvitationController;
import faang.school.projectservice.dto.stageInvitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.CreateStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.RejectStageInvitationDto;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @InjectMocks
    private StageInvitationController stageInvitationController;

    @Mock
    private StageInvitationService stageInvitationService;

    private CreateStageInvitationDto createStageInvitationDto;
    private AcceptStageInvitationDto acceptStageInvitationDto;
    private RejectStageInvitationDto rejectStageInvitationDto;
    private InvitationFilterDto invitationFilterDto;
    private Long userId;

    @BeforeEach
    public void setUp() {
        acceptStageInvitationDto = AcceptStageInvitationDto.builder().id(1L).build();
        rejectStageInvitationDto = RejectStageInvitationDto.builder().id(1L).explanation("text").build();
        createStageInvitationDto = CreateStageInvitationDto.builder().id(1L).stageId(1L).invitedId(1L).build();
        invitationFilterDto = InvitationFilterDto.builder().stageId(1L).authorId(1L).status(StageInvitationStatus.PENDING).build();
        userId = 1L;
    }

    @Test
    public void testCorrectWorkCreateInvitation() {
        assertDoesNotThrow(() -> stageInvitationController.createInvitation(createStageInvitationDto));
    }

    @Test
    public void testCorrectWorkAcceptInvitation() {
        assertDoesNotThrow(() -> stageInvitationController.acceptInvitation(acceptStageInvitationDto));
    }

    @Test
    public void testCorrectWorkRejectInvitation() {
        assertDoesNotThrow(() -> stageInvitationController.rejectInvitation(rejectStageInvitationDto));
    }

    @Test
    public void testCorrectWorkShowAllInvitationForMember() {
        assertDoesNotThrow(() -> stageInvitationController.showAllInvitationForMember(userId, invitationFilterDto));
    }
}
