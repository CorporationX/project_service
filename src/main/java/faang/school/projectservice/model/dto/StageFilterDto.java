package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.ProjectStatus;
import faang.school.projectservice.model.enums.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto {
    private ProjectStatus projectStatus;
    private TeamRole teamRole;
}