package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipFilterDto {
    private InternshipStatus internshipStatus;
    private TeamRole role;
}
