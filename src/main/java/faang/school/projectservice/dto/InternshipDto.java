package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Internship entity")
public class InternshipDto {

    private Long id;

    @NotBlank(message = "Internship's name can't be empty")
    @Size(max = 255, message = "The name length should not exceed 255 characters")
    private String name;

    @NotBlank(message = "Internship's desc can't be empty")
    private String description;

    @NotNull(message = "Project ID can't be null")
    private Long projectId;

    @NotNull(message = "Mentor ID can't be null")
    private Long mentorId;

    @NotNull(message = "Candidate's list can't be null")
    private List<Long> internIds;

    @NotNull(message = "Internship's start date can't be null")
    private LocalDateTime startDate;

    @NotNull(message = "Internship's end date can't be null")
    private LocalDateTime endDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "Created by can't be null")
    private Long createdBy;

    private Long updatedBy;

    @Size(max = 50, message = "Status cannot be more than 50 characters")
    private InternshipStatus status;
    private List<Long> candidateIds;
    private Long scheduleId;
}
