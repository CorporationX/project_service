package faang.school.projectservice.publishers;

import faang.school.projectservice.model.events.FundRaisedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundRaisedEventPublisher {
    private final KafkaTemplate<String, FundRaisedEvent> kafkaTemplate;
    @Value("${spring.kafka.fund-raised.topic}")
    private String topic;

    public void publish(FundRaisedEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
