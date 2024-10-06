package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CreateMembersDto {
    @NotNull(message = "Role can't be null")
    private TeamRole role;
    @NotEmpty(message = "User IDs can't be null/empty")
    private List<Long> userIds;
}
