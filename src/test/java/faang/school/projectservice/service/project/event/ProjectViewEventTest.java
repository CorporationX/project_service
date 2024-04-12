package faang.school.projectservice.service.project.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class ProjectViewEventTest {
    @Mock
    private ProjectViewEvent projectViewEvent;
    @Mock
    private ProjectViewEventPublisher projectViewEventPublisher;

    @Test
    void testPublishProjectViewEventPublisher() throws JsonProcessingException {
        Long userId = 10L;
        Long projectId = 20L;
        LocalDateTime timestamp = LocalDateTime.now();
        ProjectViewEventPublisher mockPublisher = Mockito.mock(ProjectViewEventPublisher.class);
        ProjectViewEvent projectViewEvent = new ProjectViewEvent(mockPublisher);

        projectViewEvent.publishProjectViewEvent(userId, projectId, timestamp);

        Mockito.verify(mockPublisher, Mockito.times(1)).publish(Mockito.anyString());
    }
}
