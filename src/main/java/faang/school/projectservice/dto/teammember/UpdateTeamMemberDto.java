package faang.school.projectservice.dto.teammember;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamMemberDto {
    private Long id;
    private List<TeamRole> roles;
    private String nickname;
}
