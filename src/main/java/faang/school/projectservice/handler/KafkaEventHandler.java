package faang.school.projectservice.handler;

import faang.school.projectservice.dto.TeamEvent;

public interface KafkaEventHandler {
  void handle(TeamEvent event);
}