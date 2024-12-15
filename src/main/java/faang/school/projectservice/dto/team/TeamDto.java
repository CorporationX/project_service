package faang.school.projectservice.dto.team;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamDto {
    private Long id;
    private Long projectId;
}
