package faang.school.projectservice.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {
    private Long id;
    private List<Long> teamMemberIds;
    private Long projectId;
}
