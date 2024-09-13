package faang.school.projectservice.service.stageinvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;

    public void save(StageInvitation stageInvitation) {
        stageInvitationRepository.save(stageInvitation);
    }
}
