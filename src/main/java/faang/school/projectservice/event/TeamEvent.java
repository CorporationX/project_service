package faang.school.projectservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TeamEvent {
    private Long authorId;
    private Long projectId;
    private Long teamId;
}
