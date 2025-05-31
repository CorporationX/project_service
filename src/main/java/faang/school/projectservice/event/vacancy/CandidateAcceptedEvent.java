package faang.school.projectservice.event.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class CandidateAcceptedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final Long candidateId;
    private final Long vacancyId;
    private final Long teamId;

    @Override
    public VacancyStatus getEventType() {
        return null;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of();
    }
}
