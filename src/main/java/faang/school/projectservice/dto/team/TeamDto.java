package faang.school.projectservice.dto.team;

import lombok.Data;

import java.util.List;

@Data
public class TeamDto {
    private Long id;
    private List<Long> teamMembersId;
    private Long projectId;
}
