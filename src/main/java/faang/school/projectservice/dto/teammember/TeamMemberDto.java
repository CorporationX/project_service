package faang.school.projectservice.dto.teammember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDto {
    private Long id;
    private Long userId;
    private Long teamId;
}
