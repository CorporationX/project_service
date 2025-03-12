package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.List;

public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private List<Long> children;
    private List<Long> stages;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
