package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Data
@Getter
@Setter
public class CreateMembersDto {
    @NotNull(message = "role can't be null")
    private TeamRole role;
    @NotEmpty(message = "userIds can't be empty")
    private List<Long> userIds;
}
