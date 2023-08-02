package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.DtoStageInvitation;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationMapper mapper = StageInvitationMapper.INSTANCE;

    private final StageInvitationRepository invitationRepository;

    public String invitationHasBeenSent(DtoStageInvitation dto) {
        invitationRepository.save(mapper.toStageInvitation(dto));
        return "приглаение отправлено";
    }

    public String acceptInvitation(String status, long idInvitation) {
        StageInvitation invitation = invitationRepository.findById(idInvitation);
        if (status.equals("ACCEPTED")) {
            invitation.setStatus(StageInvitationStatus.ACCEPTED);
            invitationRepository.save(invitation);
            return "приглашение принято";
        }
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitationRepository.save(invitation);
        return "приглашение отклонено";
    }

}
