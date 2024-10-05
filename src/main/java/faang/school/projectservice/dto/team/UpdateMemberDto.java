package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateMemberDto {
    private String nickname;
    private TeamRole role;
}
