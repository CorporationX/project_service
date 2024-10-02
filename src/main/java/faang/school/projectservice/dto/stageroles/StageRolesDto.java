package faang.school.projectservice.dto.stageroles;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageRolesDto {
    @NotNull
    private long id;
    @NotEmpty
    private TeamRole teamRole;
    @NotNull
    private int count;
}
