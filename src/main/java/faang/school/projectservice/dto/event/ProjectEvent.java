package faang.school.projectservice.dto.event;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProjectEvent {
    long authorId;
    long projectId;
}
