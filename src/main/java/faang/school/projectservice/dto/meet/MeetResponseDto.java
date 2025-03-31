package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetResponseDto {

    private long id;
    private String title;
    private String description;
    private MeetStatus status;
    private long creatorId;
    private long projectId;
    private List<Long> userIds;
    private LocalDateTime startsAt;
}
