package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisibilitySubprojectDto {
    @NotNull
    private Long id;
    @NotNull
    private ProjectVisibility visibility;
}
