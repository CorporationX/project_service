package faang.school.projectservice.kafka.producer;

import faang.school.projectservice.kafka.events.FundRaisedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundRaisedEventProducer {

    private final KafkaTemplate<String, FundRaisedEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.fundraise}")
    private String topic;

    public void sendEvent(FundRaisedEvent event) {
        kafkaTemplate.send(topic, event);
        log.info("Sent event to Kafka: {}", event);
    }
}
