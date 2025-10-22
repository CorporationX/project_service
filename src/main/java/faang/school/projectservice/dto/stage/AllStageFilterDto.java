package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record AllStageFilterDto(
        @NotNull(message = "Specify project!")
        Long projectId,
        List<@NotNull TeamRole> teamRoleList,
        TaskStatus taskStatus
        ) {
}