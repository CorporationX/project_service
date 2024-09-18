package faang.school.projectservice.dto.subproject.request;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatingRequest {

    @Pattern(regexp = "^\\S.*$", message = "Name must not be empty, but can be null")
    private String name;

    @Pattern(regexp = "^\\S.*$", message = "Description must not be empty, but can be null")
    private String description;

    private ProjectStatus status;

    private ProjectVisibility visibility;
}
