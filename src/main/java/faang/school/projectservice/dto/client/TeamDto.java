package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TeamDto {

    @Positive
    private Long id;

    @NotNull
    private ProjectDto project;

    private List<TeamMemberDto> teamMembers;
}
