package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.StageInvitationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class StageInvitationController {
  private StageInvitationService stageInvitationService;
  private void validateInvitationRequest(StageInvitationDto invitationDto) {
    if (invitationDto.getStageId() == null && invitationDto.getInvitedPersonId() == null && invitationDto.getAuthorId() == null) {
      throw new DataValidationException("Validate failed");
    }
  }

  public StageInvitationDto create(StageInvitationDto invitationDto) {
    validateInvitationRequest(invitationDto);

    return stageInvitationService.create(invitationDto);
  }
}
