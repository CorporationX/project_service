package faang.school.projectservice.dto.stage;

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
    private String stageName;
    private Long projectId;
    private List<Long> stageRoleIds;
    private List<Long> taskIds;
    private List<Long> executorIds;
}
