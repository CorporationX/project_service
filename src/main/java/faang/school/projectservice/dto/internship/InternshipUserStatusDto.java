package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipInternStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Entity of interns status")
public class InternshipUserStatusDto {
    @Schema(description = "Intern id", example = "1")
    private Long id;

    @Schema(description = "If intern finished internship ahead of schedule", example = "true")
    private boolean aheadOfSchedule;

    @Schema(description = "Status of internship", example = "PASSED", allowableValues = {"PASSED", "FAILED"})
    private InternshipInternStatus status;
}
