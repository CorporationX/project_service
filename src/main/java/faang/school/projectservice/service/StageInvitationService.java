package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
  private final StageInvitationRepository stageInvitationRepository;
  private final StageInvitationMapper stageInvitationMapper;

  public StageInvitationDto create(StageInvitationDto invitationDto) {
    StageInvitation stageInvitation = stageInvitationRepository.save(stageInvitationMapper.toEntity(invitationDto));
    return stageInvitationMapper.toDto(stageInvitation);
  }

  public List<StageInvitationDto> getAllByInvitedUserId(Long userId) {
    return stageInvitationRepository.findAll().stream()
      .filter((stageInvitation -> Objects.equals(stageInvitation.getInvited().getUserId(), userId)))
      .map(stageInvitationMapper::toDto)
      .toList();
  }

  public void acceptInvitation(Long userId, Long invitationId) {
    StageInvitation invitation = stageInvitationRepository.findById(invitationId);

    if (invitation == null) {
      throw new NotFoundException("Invitation with provided id: " + invitationId + " are not exist");
    }

    if (!Objects.equals(invitation.getInvited().getId(), userId)) {
      throw new DataValidationException("Provided invitation doesn't have user with provided ID");
    }

    invitation.setStatus(StageInvitationStatus.ACCEPTED);
    stageInvitationRepository.save(invitation);
  }

  public void declinedInvitation(Long userId, Long invitationId, String cancelDescription) {
    StageInvitation invitation = stageInvitationRepository.findById(invitationId);

    if (invitation == null) {
      throw new NotFoundException("Invitation with provided id: " + invitationId + " are not exist");
    }

    if (!Objects.equals(invitation.getInvited().getId(), userId)) {
      throw new DataValidationException("Provided invitation doesn't have user with provided ID");
    }

    invitation.setStatus(StageInvitationStatus.REJECTED);
    stageInvitationRepository.save(invitation);
  }
}
