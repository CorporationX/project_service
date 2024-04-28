package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateEventDto {
    @NotNull
    private Long projectId;
    @NotNull
    private Long userId;
}
