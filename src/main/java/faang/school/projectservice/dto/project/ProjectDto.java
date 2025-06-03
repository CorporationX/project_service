package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private String status;
    private ProjectVisibility visibility;
    private Long ownerId;
    private List<Task> tasks;
    private List<Stage> stages;
    private List<Resource> resources;

}