package faang.school.projectservice.dto.event;

import faang.school.projectservice.model.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Event transferred to presentation layer")
public class EventOutputDto {
    @Schema(description = "Database event id")
    private long id;
    @Schema(description = "Project id related to event")
    private long projectId;
    @Schema(description = "Event title")
    private String title;
    @Schema(description = "Event description")
    private String description;
    @Schema(description = "Event creator user id")
    private long creatorId;
    @Schema(description = "Event status", allowableValues = {"PENDING", "COMPLETED", "CANCELLED"})
    private EventStatus status;
    @Schema(description = "Event participants user ids")
    private List<Long> userIds;
    @Schema(description = "Event starts date time", example = "15.06.2025 14:30", pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime startsAt;
    @Schema(description = "Event ends date time", example = "15.06.2025 14:30", pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime endsAt;
    @Schema(description = "Event creation date time", example = "15.06.2025 14:30", pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;
    @Schema(description = "Event last update date time", example = "15.06.2025 14:30", pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime updatedAt;
}