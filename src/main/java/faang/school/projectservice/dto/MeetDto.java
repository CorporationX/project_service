package faang.school.projectservice.dto;

import faang.school.projectservice.model.MeetStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class MeetDto {
    private Long id;
    private String title;
    private String description;
    private MeetStatus status;
    private Long creatorId;
    private Long projectId;
    private Collection<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
