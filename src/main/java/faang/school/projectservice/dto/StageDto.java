package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {
    private Long stageId;
    @NotBlank
    private String stageName;
    @Positive
    private Long projectId;
    @NotNull
    private List<StageRoleDto> stageRoles;
    private List<Long> teamMemberIds;
}

