package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private ProjectDto project;
    private List<StageRolesDto> stageRoles;
}

