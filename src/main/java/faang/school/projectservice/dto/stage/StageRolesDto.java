package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StageRolesDto {

    private String teamRole;
    private Integer count;
}
