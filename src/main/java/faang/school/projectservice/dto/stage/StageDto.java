package faang.school.projectservice.dto.stage;

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
public class StageDto {
    private long stageId;
    private String stageName;
    private String stageStatus;
    private long projectId;
    private List<Long> stageRolesIds;
    private List<Long> tasksIds;
    private List<Long> executorsIds;
}