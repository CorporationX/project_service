package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class StageDto {
    private final long stageId;

    @NotBlank(message = "stage is unnamed")
    private final String stageName;

    private final long projectId;

    @NotEmpty(message = "stage have no roles")
    private final List<StageRolesDto> stageRoles;

    private List<TeamMemberDto> executorsDtos;
}

