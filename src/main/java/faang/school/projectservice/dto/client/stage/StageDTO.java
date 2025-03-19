package faang.school.projectservice.dto.client.stage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDTO {
    private Long id;
    @Size(min = 3, max = 100)
    private String stageName;
    private Long projectId;
    private List<Long> tasksIds;
    private List<Long> stageRoleIds;
    private List<Long> executorsIds;

}
