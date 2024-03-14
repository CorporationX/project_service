package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.TaskCompletedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TaskCompletedEventPublisher extends AbstractEventPublisher<TaskCompletedEvent>{
    @Value("${spring.data.redis.channels.task_channel.name}")
    private String channelName;

    public void publish(TaskCompletedEvent event){
        publish(event,channelName);
    }

}
