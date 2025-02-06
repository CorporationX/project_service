package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternshipFilterDto {
    @NotEmpty
    private InternshipStatus status;
    @NotEmpty
    private TeamRole role;
}
