package faang.school.projectservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteSentEventDto {
    private int authorId;
    private int invitedId;
    private int projectId;
}
