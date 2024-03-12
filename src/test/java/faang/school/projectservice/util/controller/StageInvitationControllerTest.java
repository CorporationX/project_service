package faang.school.projectservice.util.controller;

import faang.school.projectservice.controller.StageInvitationController;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StageInvitationControllerTest {

    @Mock
    private StageInvitationService stageInvitationService;
    @InjectMocks
    private StageInvitationController stageInvitationController;

    @Test
    void testCreateInvitationMissingStage() {
        StageInvitationDto stageInvitationDto = new StageInvitationDto();
        stageInvitationDto.setAuthor(new TeamMember());
        stageInvitationDto.setInvited(new TeamMember());
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.createInvitation(stageInvitationDto));
    }

    @Test
    void testCreateInvitationMissingAuthor() {
        StageInvitationDto stageInvitationDto = new StageInvitationDto();
        stageInvitationDto.setStage(new Stage());
        stageInvitationDto.setInvited(new TeamMember());
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.createInvitation(stageInvitationDto));
    }

    @Test
    void testCreateInvitationMissingInvited() {
        StageInvitationDto stageInvitationDto = new StageInvitationDto();
        stageInvitationDto.setStage(new Stage());
        stageInvitationDto.setAuthor(new TeamMember());
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.createInvitation(stageInvitationDto));
    }

    @Test
    void testAcceptInvitationMissingUserId() {
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.acceptInvitation(null, 4L));
    }

    @Test
    void testAcceptInvitationMissingInvitationId() {
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.acceptInvitation(5L, null));
    }

    @Test
    void testRejectInvitationMissingUserId() {
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.rejectInvitation(null, 4L, "anyString()"));
    }

    @Test
    void testRejectInvitationMissingInvitationId() {
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.rejectInvitation(5L, null, "anyString()"));
    }

    @Test
    void testRejectInvitationMissingDescription() {
        Assert.assertThrows(NullPointerException.class,
                () -> stageInvitationController.rejectInvitation(4L, 5L, null));
    }

}
