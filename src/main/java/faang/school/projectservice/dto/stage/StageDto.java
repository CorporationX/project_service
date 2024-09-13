package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.team.TeamMemberDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StageDto {
    @NotBlank(message = "stageName should not be blank")
    private String stageName;

    private long projectId;

    private List<Long> tasks;

    private List<StageRolesDto> stageRoles;

    private List<TeamMemberDto> executors;
}
