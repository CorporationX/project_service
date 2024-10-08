package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDto {
    private Long id;

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotEmpty(message = "Roles must not be empty")// ID пользователя
    private List<TeamRole> roles;      // Список ролей участника
}