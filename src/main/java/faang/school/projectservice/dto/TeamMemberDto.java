package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
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
    private Long userId;               // ID пользователя
    private List<TeamRole> roles;      // Список ролей участника
}