package faang.school.projectservice.kafka.producer;

import faang.school.projectservice.dto.kafka.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectViewProducer {
    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    public void sendToKafka(ProjectViewEvent projectViewEvent) {
        String key = projectViewEvent.projectId().toString() + projectViewEvent.viewerId().toString();
        objectKafkaTemplate.send("project-view", key, projectViewEvent);
        log.info("Новый ProjectViewEvent с projectId: {} и viewerId: {} отправлен в Kafka.",
                projectViewEvent.projectId(),
                projectViewEvent.viewerId()
                );
    }
}
