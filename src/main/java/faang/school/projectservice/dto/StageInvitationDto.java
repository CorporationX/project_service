package faang.school.projectservice.dto;

import lombok.Data;

@Data
public class StageInvitationDto {
  Long stageId;
  Long authorId;
  Long invitedPersonId;
}
