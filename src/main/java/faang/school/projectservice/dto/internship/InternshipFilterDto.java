package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Entity to filter internship")
public class InternshipFilterDto {
    @Schema(description = "Project id", example = "1")
    private Long projectId;

    @Schema(description = "Mentor id", example = "1")
    private Long mentorId;

    @Schema(description = "Internship status", allowableValues = { "IN_PROGRESS", "COMPLETED" })
    private InternshipStatus status;
}
