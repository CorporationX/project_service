package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamMemberDto {

    @Positive
    private Long userId;

    @NotNull
    private TeamDto team;
}
