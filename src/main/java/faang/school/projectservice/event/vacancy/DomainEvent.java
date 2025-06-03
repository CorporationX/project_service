package faang.school.projectservice.event.vacancy;

import faang.school.projectservice.model.VacancyStatus;

import java.time.Instant;
import java.util.Map;

public interface DomainEvent {
    VacancyStatus getEventType();

    Instant getOccurredAt();

    Map<String, Object> getPayload();
}
