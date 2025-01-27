package faang.school.projectservice.service.meet.publisher;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.service.meet.event.MeetCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetEventPublisherAdapter implements MeetEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishMeetCreated(Meet meet) {
        eventPublisher.publishEvent(new MeetCreateEvent(meet));
    }

    @Override
    public void publishMeetUpdated(Meet meet) {

    }

    @Override
    public void publishMeetDeleted(Meet meet) {

    }
}
