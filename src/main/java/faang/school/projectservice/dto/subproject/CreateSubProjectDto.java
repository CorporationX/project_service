package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.project.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateSubProjectDto(
        Long id,
        @NotBlank String name,
        @NotNull @Positive Long ownerId,
        @NotNull ProjectVisibility visibility,
        List<StageDto> stages
) {
}
