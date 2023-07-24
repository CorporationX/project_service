package faang.school.projectservice.dto.client.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    private String StageName;
    private Long projectId;
    private List<StageRolesDto> stageRoles;
}
