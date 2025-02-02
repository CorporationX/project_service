package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class InternshipFilterDto {
    private InternshipStatus status;
    private TeamRole role;
}
