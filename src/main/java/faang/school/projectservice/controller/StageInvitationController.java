package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
  private final StageInvitationService stageInvitationService;

  private void validateStageId(Long id) {
    if (id == null) {
      throw new DataValidationException("Stage id is required");
    }
  }

  private void validateInvitedPersonId(Long id) {
    if (id == null) {
      throw new DataValidationException("Invited person id is required");
    }
  }

  private void validateAuthorId(Long id) {
    if (id == null) {
      throw new DataValidationException("Invited person id is required");
    }
  }

  private void validateInvitationRequest(StageInvitationDto invitationDto) {
    validateStageId(invitationDto.getStageId());
    validateInvitedPersonId(invitationDto.getInvitedPersonId());
    validateAuthorId(invitationDto.getAuthorId());
  }

  public StageInvitationDto create(StageInvitationDto invitationDto) {
    validateInvitationRequest(invitationDto);

    return stageInvitationService.create(invitationDto);
  }

  public List<StageInvitationDto> getAllByInvitedUserId(Long userId) {
    if (userId == null) {
      throw new DataValidationException("Invited user id is required");
    }

    return stageInvitationService.getAllByInvitedUserId(userId);
  }
}
