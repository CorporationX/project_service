package faang.school.projectservice.dto.stageroles;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
