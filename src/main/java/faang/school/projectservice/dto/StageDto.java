package faang.school.projectservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<Long> taskIds;
    private List<StageRolesDto> stageRolesDtos;
}
