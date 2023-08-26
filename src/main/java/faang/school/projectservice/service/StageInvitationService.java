package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.publisher.InviteSentEvent;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final InviteSentEvent inviteSentEvent;

    public void createStageInvitation(StageInvitation stageInvitation) {
        stageInvitationRepository.save(stageInvitation);
        inviteSentEvent.publish(stageInvitation);
    }

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        StageInvitation stageInvitationSaved = stageInvitationRepository.save(stageInvitation);

        inviteSentEvent.publish(stageInvitationSaved);

        return stageInvitationMapper.toDto(stageInvitationSaved);
    }
}
