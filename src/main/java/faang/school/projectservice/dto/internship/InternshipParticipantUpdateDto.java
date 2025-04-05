package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InternshipParticipantUpdateDto {
    @NotNull(message = "Intern ID is required")
    private Long internId;
    private Boolean passed;
    private TeamRole newRole;
}
