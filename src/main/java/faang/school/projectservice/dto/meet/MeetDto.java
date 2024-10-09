package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class MeetDto {
    private long id;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must be less than or equal to 255 characters")
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 255, message = "Description must be less than or equal to 255 characters")
    private String description;

    @NotNull(message = "Start date must not be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date must not be null")
    private LocalDateTime endDate;

    @NotNull(message = "Status must not be null")
    private MeetStatus status;

    @NotNull(message = "Creator ID must not be null")
    private Long creatorId;

    @NotNull(message = "Project ID must not be null")
    private long projectId;

    private long googleEventId;
    private List<Long> userIds;
}