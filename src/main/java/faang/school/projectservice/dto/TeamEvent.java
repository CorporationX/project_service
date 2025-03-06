package faang.school.projectservice.dto;

import lombok.Data;

@Data
public class TeamEvent {
  private Long authorId;
  private Long projectId;
  private Long teamId;
}