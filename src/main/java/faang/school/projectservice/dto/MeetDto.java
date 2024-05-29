package faang.school.projectservice.dto;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Builder
public class MeetDto {

    @NotBlank(message = "Meet name can't be empty")
    @Size(max = 128, message = "The name length should not exceed 128 characters")
    private String name;

    @Size(max = 4096, message = "The description length should not exceed 4096 characters")
    private String description;

    @Size(max = 512, message = "The location length should not exceed 512 characters")
    private String location;

    @Size(max = 255, message = "The timezone length should not exceed 128 characters")
    private String timeZone;

    private String eventGoogleId;
    private String eventGoogleUrl;

    @NotNull(message = "User Id in meet must be set and can't be 0")
    @Min(value = 1, message = "User Id in meet must be set and can't be 0")
    private Long createdBy;

    @NotNull(message = "Project Id in meet must be set and can't be 0")
    @Min(value = 1, message = "Project Id in meet must be set and can't be 0")
    private Long projectId;

    @NotNull(message = "Status can't be null")
    private MeetStatus meetStatus;

    @NotNull(message = "Start date time can't be null")
    private ZonedDateTime startDateTime;

    @NotNull(message = "End date time can't be null")
    private ZonedDateTime endDateTime;

    private LocalDateTime createdAt;
}
