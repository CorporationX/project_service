package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageRoleDto {
    private Long id;
    private TeamRole teamRole;  // Перечисление ролей в команде
    private Integer count;      // Количество людей с этой ролью
}

