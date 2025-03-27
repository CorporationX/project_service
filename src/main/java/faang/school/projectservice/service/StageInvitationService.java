package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
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

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;

    public StageInvitationDto sendInvitation(StageInvitationDto invitationDto) {
        StageInvitation invitation = stageInvitationMapper.toEntity(invitationDto);
        invitation.setStatus(StageInvitationStatus.PENDING);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(invitation));
    }

    public StageInvitationDto acceptInvitation(long invitationId) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation not found");
        }
        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is not pending");
        }
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(invitation));
    }

    public StageInvitationDto rejectInvitation(long invitationId, String reason) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation not found");
        }
        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is not pending");
        }
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(invitation));
    }

    public List<StageInvitationDto> getAllInvitations(long stageId) {
        return stageInvitationRepository.findAll().stream()
                .filter(invitation -> invitation.getStage().equals(stageId))
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}