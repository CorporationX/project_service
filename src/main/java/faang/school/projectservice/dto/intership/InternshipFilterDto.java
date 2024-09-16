package faang.school.projectservice.dto.intership;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InternshipFilterDto {
    private TeamRole teamRole;
    private InternshipStatus internshipStatus;
}
