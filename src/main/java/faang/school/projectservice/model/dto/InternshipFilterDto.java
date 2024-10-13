package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.InternshipStatus;
import faang.school.projectservice.model.enums.TeamRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InternshipFilterDto {
    private InternshipStatus statusPattern;
    private TeamRole rolePattern;
}