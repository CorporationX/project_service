package faang.school.projectservice.dto.project;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProjectChannelEventDto {
    private long authorId;
    private long projectId;
}
