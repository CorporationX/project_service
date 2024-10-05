package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CreateMembersDto {
    @NotBlank(message = "Team role can't be null/empty")
    private TeamRole role;
    @NotEmpty(message = "User IDs can't be null/empty")
    private List<Long> userIds;
}
