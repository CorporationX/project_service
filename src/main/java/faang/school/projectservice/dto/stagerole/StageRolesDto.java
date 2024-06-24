package faang.school.projectservice.dto.stagerole;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validation.enumvalidator.EnumValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StageRolesDto {

    @NotNull(message = "Id should not be null")
    @Positive(message = "Id should be positive")
    private Long id;

    @NotNull(message = "TeamRole should not be null")
    @EnumValidator(enumClass = TeamRole.class, message = "Invalid Team Role")
    private String teamRole;

    @NotNull(message = "Count should not be null")
    @Positive(message = "Count should be positive")
    private Integer count;

    @NotNull(message = "StageId should not be null")
    @Positive(message = "StageId should be positive")
    private Long stageId;
}
