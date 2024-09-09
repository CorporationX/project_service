package faang.school.projectservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class StageDto {
    private final long stageId;
    private final String stageName;
    private final long projectId;
    private final List<StageRolesDto> stageRoles;
    private List<TeamMemberDto> executorsDtos;
}

