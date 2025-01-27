package faang.school.projectservice.service.meet.publisher;

import faang.school.projectservice.model.Meet;

public interface MeetEventPublisher {
    void publishMeetCreated(Meet meet);

    void publishMeetUpdated(Meet meet);

    void publishMeetDeleted(Meet meet);
}
