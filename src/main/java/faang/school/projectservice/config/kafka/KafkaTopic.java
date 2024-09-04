package faang.school.projectservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Value("${spring.kafka.topics.project_topic}")
    private String projectTopicName;

    @Bean
    public NewTopic projectTopic() {
        return TopicBuilder.name(projectTopicName).build();
    }

}
