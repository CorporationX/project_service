package faang.school.projectservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TeamEvent {
  private Long authorId;
  private Long projectId;
  private Long teamId;
}