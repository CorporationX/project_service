package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StageRolesDto {
    private Long id;
    private TeamRole teamRole;
    private Integer count;
    private StageDto stage;
}