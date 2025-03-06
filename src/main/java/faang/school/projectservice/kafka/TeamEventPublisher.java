package faang.school.projectservice.kafka;

import faang.school.projectservice.properties.ProjectServiceProperties;
import faang.school.projectservice.dto.TeamEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamEventPublisher {
  private final KafkaTemplate<String, TeamEvent> kafkaTemplate;
  private final ProjectServiceProperties properties;

  public void publishTeamEvent(TeamEvent event) {
    kafkaTemplate.send(properties.getKafka().getTopic(), event);
  }
}