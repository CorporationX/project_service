package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Team;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamMemberDto {
    private Long userId;
    private TeamDto team;
}
