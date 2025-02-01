package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import lombok.Builder;

import java.util.List;

@Builder
public record SubProjectDto(
        Long id,
        String title,
        ProjectVisibility visibility,
        Stage stage,
        List<Project>projects
){}
