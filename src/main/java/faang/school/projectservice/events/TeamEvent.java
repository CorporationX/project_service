package faang.school.projectservice.events;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamEvent {
    private Long teamId;
    private Long authorId;
    private Long projectId;

}
