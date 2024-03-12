package faang.school.projectservice.dto.client.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEventDto {
    private long userId;
    private long projectId;
    private long teamId;
}