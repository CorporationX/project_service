package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.StageRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDto {
    private Long id;
    private String name;
    private Long projectId;
    private List<StageRoles> roles;
}
