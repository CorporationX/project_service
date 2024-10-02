package faang.school.projectservice.dto.filter.stage;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.dto.stageroles.StageRolesDto;
import faang.school.projectservice.dto.task.TaskDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto extends FilterDto {
    private String stageName;
    private long projectId;
    private List<StageRolesDto> stageRolesDtos;
    private List<TaskDto> taskDtos;
}
