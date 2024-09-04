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
public class ProjectEvent {
    private final UUID eventId = UUID.randomUUID();
    private final LocalDateTime timeStamp = LocalDateTime.now();
    private long authorId;
    private long projectId;
}
