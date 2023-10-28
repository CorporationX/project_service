package faang.school.projectservice.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeamMemberDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private List<TeamRole> roles;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long teamId;
}
