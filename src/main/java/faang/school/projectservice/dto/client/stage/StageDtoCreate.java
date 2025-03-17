package faang.school.projectservice.dto.client.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDtoCreate {
    private Long id;
    @NotNull
    @Size(min = 3, max = 100)
    private String stageName;
    private HashMap<@NotNull TeamRole, @Min(1) Integer> roleAndCount;

}
