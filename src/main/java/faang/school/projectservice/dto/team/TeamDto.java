package faang.school.projectservice.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {

    private Long id;
    private List<Long> teamMembersId;
    private List<Long> teamMeetsId;
    private Long projectId;
}
