package faang.school.projectservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectViewEventDto {
    private long projectId;
    private long userId;
    private LocalDateTime viewTime;
}