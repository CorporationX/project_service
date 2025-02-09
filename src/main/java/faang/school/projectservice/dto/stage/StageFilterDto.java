package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageFilterDto {
    private Set<TeamRole> role;
    private TaskStatus taskStatus;
}