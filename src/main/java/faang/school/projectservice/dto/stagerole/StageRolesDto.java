package faang.school.projectservice.dto.stagerole;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.enumvalidator.EnumValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageRolesDto {
    private Long id;
    @EnumValidator(enumClass = TeamRole.class, message = "Invalid Team Role")
    private String teamRole;
    @Positive
    @NotNull
    private Integer count;
    @PositiveOrZero
    @NotNull
    private Long stageId;
}
