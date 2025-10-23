package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record AllStageFilterDto(
        @NotNull(message = "Specify project!")
        @Positive(message = "The stage must be positive!")
        Long projectId,
        List<@NotNull TeamRole> teamRoleList,
        TaskStatus taskStatus
) {
}