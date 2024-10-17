package faang.school.projectservice.dto.project;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record ProjectViewEvent(long projectId, long userId, LocalDateTime viewTime) implements Serializable {
}
