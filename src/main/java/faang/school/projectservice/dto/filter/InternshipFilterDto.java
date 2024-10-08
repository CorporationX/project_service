package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InternshipFilterDto {
    private InternshipStatus statusPattern;
    private TeamRole rolePattern;
}
