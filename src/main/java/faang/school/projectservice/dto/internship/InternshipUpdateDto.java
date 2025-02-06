package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;

@Data
@Schema(description = "Entity to update internship")
public class InternshipUpdateDto {
    @Schema(description = "Internship id", example = "1")
    private Long id;

    @Schema(description = "Mentor id", example = "1")
    private Long mentorId;

    @Schema(description = "Interns Information")
    private List<InternshipUserStatusDto> interns;

    @Schema(description = "Role for intern", allowableValues = {"DEVELOPER", "DESIGNER", "TESTER", "ANALYST",
            "MANAGER"})
    private TeamRole role;

    @Schema(description = "Internship status", allowableValues = { "IN_PROGRESS", "COMPLETED" })
    private InternshipStatus status;
}
