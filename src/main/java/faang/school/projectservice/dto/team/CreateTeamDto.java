package faang.school.projectservice.dto.team;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateTeamDto {
    private Long id;
    private LocalDateTime createdAt;
}
