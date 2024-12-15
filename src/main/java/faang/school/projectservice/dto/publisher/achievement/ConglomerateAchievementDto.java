package faang.school.projectservice.dto.publisher.achievement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConglomerateAchievementDto {
    private Long userId;
    private Long projectId;
    private Long teamId;
}
