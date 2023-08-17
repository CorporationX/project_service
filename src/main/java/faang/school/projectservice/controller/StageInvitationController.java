package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.StageInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Tag(name="StageInvitation", description = "")
@Controller
@RequiredArgsConstructor
public class StageInvitationController {
  private final StageInvitationService stageInvitationService;

  private void validateRequiredField(Long id, String message) {
    if (id == null) {
      throw new DataValidationException(message);
    }
  }

  private void validateRequiredField(String field, String message) {
    if (field == null) {
      throw new DataValidationException(message);
    }
  }

  private void validateStageId(Long id) {
    validateRequiredField(id, "Stage id is required");
  }

  private void validateInvitedPersonId(Long id) {
    validateRequiredField(id, "Invited person id is required");
  }

  private void validateAuthorId(Long id) {
    validateRequiredField(id, "User id is required");
  }

  private void validateInvitationRequest(StageInvitationDto invitationDto) {
    validateStageId(invitationDto.getStageId());
    validateInvitedPersonId(invitationDto.getInvitedPersonId());
    validateAuthorId(invitationDto.getAuthorId());
  }

  @Operation(
          summary = "",
          tags = { "stageInvitation", "post" })
  public StageInvitationDto create(StageInvitationDto invitationDto) {
    validateInvitationRequest(invitationDto);

    return stageInvitationService.create(invitationDto);
  }

  @Operation(
          summary = "",
          tags = { "stageInvitation", "get" })
  public List<StageInvitationDto> getAllByInvitedUserId(Long userId) {
    if (userId == null) {
      throw new DataValidationException("Invited user id is required");
    }

    return stageInvitationService.getAllByInvitedUserId(userId);
  }

  @Operation(
          summary = "",
          tags = { "stageInvitation", "" })
  public void acceptInvitation(Long userId, Long invitationId) {
    validateRequiredField(userId, "User id is required");
    validateRequiredField(invitationId, "Invitation id is required");

    stageInvitationService.acceptInvitation(userId, invitationId);
  }

  @Operation(
          summary = "",
          tags = { "stageInvitation", "" })
  public void declineInvitation(Long userId, Long invitationId, String cancelDescription) {
    validateRequiredField(userId, "User id is required");
    validateRequiredField(invitationId, "Invitation id is required");
    validateRequiredField(cancelDescription, "Cancel description is required");

    stageInvitationService.declinedInvitation(userId, invitationId, cancelDescription);
  }
}
