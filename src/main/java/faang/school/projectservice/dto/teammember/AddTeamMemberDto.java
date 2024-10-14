package faang.school.projectservice.dto.teammember;

import java.util.List;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTeamMemberDto {
    private Long userId;
    private Long projectId;
    private String nickname;
    private List<TeamRole> roles;
}
