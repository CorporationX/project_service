package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageFilterDto {

    private String stageNamePattern;
    private TeamRole teamRole;
}