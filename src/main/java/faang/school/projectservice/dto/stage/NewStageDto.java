package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.validation.enumvalidator.EnumValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class NewStageDto {

    @NotNull(message = "StageName should not be null")
    @NotBlank(message = "StageName should not be blank")
    private String stageName;

    @NotNull(message = "StageStatus should not be null")
    @EnumValidator(enumClass = StageStatus.class, message = "Invalid Stage Status")
    private String stageStatus;

    @NotNull(message = "ProjectId should not be null")
    @Positive(message = "ProjectId should be positive")
    private Long projectId;

    @NotNull(message = "StageRoles should not be null")
    private List<NewStageRolesDto> stageRoles;

    @NotNull(message = "TasksIds should not be null")
    private List<Long> tasksIds;

    @NotNull(message = "ExecutorsIds should not be null")
    private List<Long> executorsIds;
}