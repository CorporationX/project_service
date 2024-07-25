package faang.school.projectservice.dto.team;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamMemberDto {

    private Long id;
    private Long userId;
    private TeamDto team;
}
