package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stageroles.StageRolesDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageUpdateDto {
    private long stageId;
    private String stageName;
    private long projectId;
    private List<Long> stageRoleIds;
    private List<Long> taskIds;
    private List<Long> executorIds;
}
