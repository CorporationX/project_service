package faang.school.projectservice.scheduler.meet;

import faang.school.projectservice.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingCompletionScheduler {

    private final MeetService meetService;

    @Scheduled(cron = "${meet.scheduler.cron}")
    public void checkAndUpdateMeetingStatus() {
        meetService.checkAndCompletePastMeetings();
    }
}
