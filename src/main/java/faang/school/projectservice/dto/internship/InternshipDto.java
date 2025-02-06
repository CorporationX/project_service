package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Entity for internship")
public class InternshipDto {
    @Schema(description = "Internship id", example = "1")
    private Long id;

    @Schema(description = "Project id", example = "1")
    private Long projectId;

    @Schema(description = "Mentor id", example = "1")
    private Long mentorId;

    @Schema(description = "Interns")
    private List<InternshipUserInformationDto> interns;

    @Schema(description = "Start date of internship")
    private LocalDateTime startDate;

    @Schema(description = "Start date of internship")
    private LocalDateTime endDate;

    @Schema(description = "Internship status", allowableValues = { "IN_PROGRESS" })
    private InternshipStatus status;

    @Schema(description = "Description", example = "Java developers")
    private String description;

    @Schema(description = "Name", example = "Java")
    private String name;

    @Schema(description = "Create date")
    private LocalDateTime createdAt;

    @Schema(description = "Updated date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(description = "Created by")
    private Long createdBy;

    @Schema(description = "Updated by", accessMode = Schema.AccessMode.READ_ONLY)
    private Long updatedBy;

    @Schema(description = "Schedule id", example = "1")
    private Long scheduleId;
}
