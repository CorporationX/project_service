package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeetResponseDto {
    private long id;
    private String title;
    private String description;
    private MeetStatus status;
    private long creatorId;
    private long projectId;
    private LocalDateTime startsAt;
}
