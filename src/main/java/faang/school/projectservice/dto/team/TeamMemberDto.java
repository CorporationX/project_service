package faang.school.projectservice.dto.team;


import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;
@Validated
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeamMemberDto {
    @NotNull
    private Long id;
    @NotNull
    private Long userId;
    private List<TeamRole> roles;
}
