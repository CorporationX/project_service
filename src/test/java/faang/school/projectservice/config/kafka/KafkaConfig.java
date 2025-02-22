package faang.school.projectservice.config.kafka;

import faang.school.projectservice.dto.event.ProjectCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final Environment environment;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectCreateEvent> kafkaProjectCreateListenerContFactory(
            KafkaProperties kafkaProperties) {
        ConsumerFactory<String, ProjectCreateEvent> kafkaProjectCreateConsumerFactory =
                getProjectCreateConsFactory(kafkaProperties);
        ConcurrentKafkaListenerContainerFactory<String, ProjectCreateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaProjectCreateConsumerFactory);
        log.debug("kafkaProjectCreateListenerContainerFactory: {}", factory);
        return factory;
    }

    private ConsumerFactory<String, ProjectCreateEvent> getProjectCreateConsFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("spring.kafka.consumer.project-create.group-id"));
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(ProjectCreateEvent.class));
    }
}