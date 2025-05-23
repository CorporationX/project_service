package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFilterDto {

    private String name;
    private String descriptionPattern;
    private Long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
