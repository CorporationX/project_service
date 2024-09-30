package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectFilterDto {

    @NotNull(message = "Status should not be null")
    private ProjectStatus status;

    @NotNull(message = "Visibility should not be null")
    private ProjectVisibility visibility;

    @NotBlank(message = "description should not be blank")
    private String name;
}
