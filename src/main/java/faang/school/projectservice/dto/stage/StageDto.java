package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
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
    private Long projectId;
    private List<StageRolesDto> stageRolesDto;
}