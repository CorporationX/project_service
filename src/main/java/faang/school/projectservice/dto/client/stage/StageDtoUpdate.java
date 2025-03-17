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
public class StageDtoUpdate {
    private Long id;
    @NotNull
    @Size(min = 3, max = 100)
    private String stageName;
    @NotNull
    private Long projectId;
    @Size(min = 1)
    private List<Long> tasksId;
    @Size(min = 1)
    private List<Long> stageRoleId;
    @Size(min = 1)
    private List<Long> executorsId;

}
