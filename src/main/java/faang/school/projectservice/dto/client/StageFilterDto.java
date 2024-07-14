package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageFilterDto {
    private TeamRole teamRolePattern;
    private TaskStatus taskStatusPattern;
}
