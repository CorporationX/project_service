package faang.school.projectservice.dto.meet;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetDto {

    private long id;

    @NotNull(message = "Status must not be null")
    private MeetStatus status;

    @NotNull(message = "Creator ID must not be null")
    @Positive(message = "Creator ID must be a positive number")
    private Long creatorId;

    @NotNull(message = "Title must not be null")
    private String title;

    @NotNull(message = "Project ID must not be null")
    @Positive(message = "Project ID must be a positive number")
    private long projectId;

    @NotNull(message = "User IDs list must not be null")
    @Size(min = 1)
    private List<Long> userIds;

    @NotNull(message = "Description IDs list must not be null")
    private String description;

    @NotNull(message = "Start date and time must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startsAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
