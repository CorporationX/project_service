package faang.school.projectservice.service.meet.observer;

import faang.school.projectservice.service.meet.event.MeetCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GoogleCalendarObserver {

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMeetCreated(MeetCreateEvent meetCreateEvent) {

    }
}
