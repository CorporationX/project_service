package faang.school.projectservice.dto.team;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {
    private Long id;
    private List<Long> teamMemberIds;
    private Long projectId;
}
