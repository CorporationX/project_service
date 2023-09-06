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
    private long authorId;
    private long invitedId;
    private long projectId;
}
