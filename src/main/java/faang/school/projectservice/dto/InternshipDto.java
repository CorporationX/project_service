package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class InternshipDto {

    private Long id;

    @NotBlank(message = "Internship's name can't be empty")
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
    private InternshipStatus status;
    private List<Long> candidateIds;
    private Long scheduleId;
}
