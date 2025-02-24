package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    private long id;

    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blank")
    private String name;

    private String description;

    @NotNull(message = "Owner id can't be null")
    @Min(value = 1, message = "Owner id should be more than 0")
    private long ownerId;

    @NotNull(message = "Visibility can't be null")
    private String visibility;

    private String status;
}
