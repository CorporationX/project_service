package faang.school.projectservice.dto.member;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDto {
    private Long id;
    private Long userId;
    private List<String> roles;
    private Long teamId;
}
