package faang.school.projectservice.util.controller;

import faang.school.projectservice.controller.StageInvitationController;
import faang.school.projectservice.service.StageInvitationService;
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
    void testCreateInvitationMissingStage(){

    }

    @Test
    void testCreateInvitationMissingAuthor(){

    }

    @Test
    void testCreateInvitationMissingInvited(){

    }

    @Test
    void testAcceptInvitationMissingUserId(){

    }

    @Test
    void testAcceptInvitationMissingInvitationId(){

    }

    @Test
    void testRejectInvitationMissingUserId(){

    }

    @Test
    void testRejectInvitationMissingInvitationId(){

    }

    @Test
    void testRejectInvitationMissingDescription(){

    }

    @Test
    void testGetAllInvitationMissingId(){

    }

}
