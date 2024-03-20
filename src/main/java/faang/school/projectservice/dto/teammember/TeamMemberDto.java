package faang.school.projectservice.dto.teammember;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
    private Long id;
    private List<TeamRole> roles;
}
