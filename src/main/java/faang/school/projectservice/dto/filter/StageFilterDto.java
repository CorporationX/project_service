package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.filter.TaskFilterEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto {
    private TeamRole role;
    private TaskStatus status;
    private TaskFilterEnum roleFilterEnum;
}
