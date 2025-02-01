package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateSubProjectDto(
        Long id,
        List<Long> subProjectIds,
        Stage stage,
        ProjectVisibility visibility,
        Moment lastUpdate
) {
}
