package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class StageDto {
    private long stageId;
    private String stageName;
    private String stageStatus;
    private long projectId;
    private List<Long> stageRolesIds;
    private List<Long> tasksIds;
    private List<Long> executorsIds;
}