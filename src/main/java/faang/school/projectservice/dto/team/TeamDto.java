package faang.school.projectservice.dto.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TeamDto {
    private Long teamId;
    private List<Long> teamMemberIds;
    private Long projectId;
}
