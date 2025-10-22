package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record StageCreateDto(
        @NotNull(message = "Specify your project!")
        Long projectId,
        @NotNull @NotBlank(message = "Indicate the name of the Stage!")
        String stageName,
        @NotNull @NotEmpty(message = "Specify stage roles!")
        List<@NotNull TeamRole> teamRoles,
        @NotNull @NotEmpty(message = "Specify stage executors!")
        List<@NotNull Long> executorsId
) {
}