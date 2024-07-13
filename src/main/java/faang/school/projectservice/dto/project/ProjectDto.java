package faang.school.projectservice.dto.project;

import faang.school.projectservice.dto.client.task.TaskDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private List<TaskDto> tasks;
}
