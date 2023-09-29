package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectEventDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.project.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProjectPublisher extends AbstractPublisher<ProjectEventDto> {

    private final ProjectMapper projectMapper;

    public ProjectPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                            @Value("${spring.data.redis.channels.projects_channel.name}") String topicChannelName,
                            ProjectMapper projectMapper) {
        super(redisTemplate, objectMapper, topicChannelName);
        this.projectMapper = projectMapper;
    }

    public void publish(Project project) {
        ProjectEventDto event = projectMapper.toEventProjectDto(project);
        publishInTopic(event);
    }
}
