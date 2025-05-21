package faang.school.projectservice.event;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class VacancyClosedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final Long vacancyId;
    private final Long projectId;
    private final Integer filledCount;
    private final TeamRole position;

    @Override
    public VacancyStatus getEventType() {
        return VacancyStatus.CLOSED;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of(
                "vacancyId", vacancyId,
                "projectId", projectId,
                "filledCount", filledCount,
                "position", position
        );
    }
}
