package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class InternshipFiltersDto {
    private InternshipStatus statusPattern;
    private TeamRole rolePattern;
}
