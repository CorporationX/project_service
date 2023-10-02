package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResourceDto {
    private Long id;

    @NotNull(message = "ProjectId can't be null")
    private Long projectId;
}
