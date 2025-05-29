package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFilterDto {

    @Size(max = 255, message = "Name must be at most 255 characters long")
    private String name;
    @Size(max = 255, message = "Description must be at most 255 characters long")
    private String descriptionPattern;

    private Long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
