package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto {

    @NotNull(message = "Project status cannot be null")
    private ProjectStatus projectStatus;

    @NotNull(message = "Team role cannot be null")
    private TeamRole teamRole;
}