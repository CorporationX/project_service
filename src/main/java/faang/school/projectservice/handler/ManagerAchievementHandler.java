package faang.school.projectservice.handler;

import faang.school.projectservice.dto.TeamEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerAchievementHandler implements KafkaEventHandler {

  @Override
  public void handle(TeamEvent event) {
    System.out.println("Handling event: " + event);

  }
}