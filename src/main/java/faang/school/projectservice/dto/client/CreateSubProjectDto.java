package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.stage.Stage;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateSubProjectDto(
    Long parentId,
    Long id,
    List<Long> subProjectIds,
    Stage stage
){}
