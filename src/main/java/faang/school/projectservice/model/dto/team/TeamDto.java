package faang.school.projectservice.model.dto.team;

import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamDto(
        //@NotNull(message = "Id must be null", groups = {CreateGroup.class, UpdateGroup.class})
        Long id,
        @NotEmpty(message = "Team must have at least")
        List<Long> teamMemberIds,
        @NotNull(message = "projectId must be not null")
        Long projectId
) {
}
