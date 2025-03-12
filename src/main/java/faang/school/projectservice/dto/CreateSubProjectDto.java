package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

import java.util.List;

@Data
public class CreateSubProjectDto {
    private Long id;
    private Long parentProject;
    private String name;
    private String description;
    private Long ownerId;
    private List<CreateSubProjectDto> children;
    private List<String> stages;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
