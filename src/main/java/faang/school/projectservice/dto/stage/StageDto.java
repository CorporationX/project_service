package faang.school.projectservice.dto.stage;

import lombok.Data;

import java.util.List;

@Data
public class StageDto {

    private Long stageId;
    private String stageName;
    private Long projectId;
    private List <Long> stageRoleIds;
    private List<Long> taskIds;
    private List<Long> executorIds;
}