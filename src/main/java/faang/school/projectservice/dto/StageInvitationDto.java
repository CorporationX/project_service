package faang.school.projectservice.dto;

import lombok.Data;

@Data
public class StageInvitationDto {
  private Long stageId;
  private Long authorId;
  private Long invitedPersonId;
}
