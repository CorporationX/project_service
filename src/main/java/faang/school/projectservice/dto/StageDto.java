package faang.school.projectservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Data
public class StageDto {
    private final long id;
    private final long projectId;
    private final String stageName;
    private final List<StageRolesDto> stageRoles;
    private List<Long> executorsIds;
}
