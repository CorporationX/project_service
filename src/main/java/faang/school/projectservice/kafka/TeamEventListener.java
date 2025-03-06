package faang.school.projectservice.kafka;

import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.handler.KafkaEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamEventListener {
  private final List<KafkaEventHandler> eventHandlers;

  @KafkaListener(topics = "#{projectServiceProperties.kafka.topic}")
  public void listen(TeamEvent event) {
    eventHandlers.forEach(handler -> handler.handle(event));
  }
}