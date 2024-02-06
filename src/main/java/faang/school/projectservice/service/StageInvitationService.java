package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.ValidateStageInvitationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationRepository stageInvitationRepository;

    private StageInvitation validate(Long userId, Long invitationId) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);

        if (invitation == null) {
            throw new ValidateStageInvitationException("Invitation with provided id: " + invitationId + " are not exist");
        }

        if (invitation.getInvited().getId().equals(userId)) {
            throw new ValidateStageInvitationException("User " + userId + " not found in invitation");
        }

        return invitation;
    }

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.save(stageInvitationMapper.toEntity(stageInvitationDto));
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public void accept(Long userId, Long invitationId) {
        StageInvitation invitation = validate(userId, invitationId);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(invitation);
    }

    public void reject(Long userId, Long invitationId, String description) {
        StageInvitation invitation = validate(userId, invitationId);

        invitation.setDescription(description);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitationRepository.save(invitation);
    }

    public List<StageInvitationDto> getAll(Long id) {
        return stageInvitationMapper.toDto(stageInvitationRepository
                .findAll()
                .stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(id))
                .toList());
    }

}
