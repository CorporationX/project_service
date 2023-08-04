package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
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
    private Stage stage;
}
