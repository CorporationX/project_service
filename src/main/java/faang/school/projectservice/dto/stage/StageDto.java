package faang.school.projectservice.dto.stage;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Set;


public record StageDto(
        @Positive
        Long stageId,

        @NotBlank
        String stageName,

        @Positive
        Long projectId,

        @NotNull
        Set<StageRolesDto> stageRolesDtos,

        @NotNull
        List<Long> taskIds,

        @NotEmpty
        List<Long> executorIds) {
}
