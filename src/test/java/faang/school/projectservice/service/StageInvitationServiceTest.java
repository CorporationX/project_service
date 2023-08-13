package faang.school.projectservice.service;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Test
    void createStageInvitation() {
        StageInvitation build = StageInvitation.builder().build();
        stageInvitationService.createStageInvitation(build);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).save(build);
    }
}