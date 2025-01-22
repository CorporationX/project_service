package faang.school.projectservice.message.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectViewEvent(
        long projectId,
        long userId,
        LocalDateTime receivedAt
) {
}
