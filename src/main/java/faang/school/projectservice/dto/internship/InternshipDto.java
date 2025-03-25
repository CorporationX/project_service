package faang.school.projectservice.dto.internship;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipDto {
    private Long id;
    @NotNull(message = "Field projectId is required")
    private Long projectId;
    @NotNull(message = "Field mentorId is required")
    private Long mentorId;
    @NotEmpty(message = "Should have at least one intern")
    private List<Long> internIds;
    @NotNull(message = "Field startDate is required")
    private LocalDateTime startDate;
    @NotNull(message = "Field endDate is required")
    private LocalDateTime endDate;
    @JsonProperty(defaultValue = "IN_PROGRESS")
    private InternshipStatus status = InternshipStatus.IN_PROGRESS;
    @NotBlank
    private String description;
    @NotBlank
    private String name;
    @NotNull(message = "Field scheduleId is required")
    private Long scheduleId;
}
