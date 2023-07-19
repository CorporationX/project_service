package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class StageRolesDto {
    @NotNull
    private Long stageRoleId;
    @NotNull
    private Integer count;
    @NotEmpty
    private String teamRole;
    private Long stageId;
}