package faang.school.projectservice.dto.stagerole;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.enumvalidator.EnumValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NewStageRolesDto {

    @NotNull(message = "TeamRole should not be null")
    @EnumValidator(enumClass = TeamRole.class, message = "Invalid Team Role")
    private String teamRole;

    @NotNull(message = "Count should not be null")
    @Positive(message = "Count should not be null")
    private Integer count;
}
