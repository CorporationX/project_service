package faang.school.projectservice.event.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectViewEvent {

    private final UUID eventId = UUID.randomUUID();
    private long projectId;
    private long viewerId;
    private LocalDateTime timestamp;
}
