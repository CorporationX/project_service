package faang.school.projectservice.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectViewEvent {
    @NotNull
    Long userId;
    @NotNull
    Long projectId;
    LocalDateTime timestamp;
}
