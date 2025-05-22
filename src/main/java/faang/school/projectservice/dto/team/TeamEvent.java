package faang.school.projectservice.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TeamEvent {
    private Long authorId;
    private Long projectId;
    private Long teamId;
}
