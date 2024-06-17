package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.validator.enumvalidator.EnumValidator;
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
public class StageDto {
    @Positive
    private long stageId;

    @NotBlank
    private String stageName;

    @EnumValidator(enumClass = StageStatus.class, message = "Invalid Stage Status")
    private String stageStatus;

    @Positive
    private long projectId;

    @NotNull
    private List<Long> stageRolesIds;

    private List<Long> tasksIds;

    private List<Long> executorsIds;
}