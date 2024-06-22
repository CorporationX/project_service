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
public class NewStageRolesDto {
    @EnumValidator(enumClass = TeamRole.class, message = "Invalid Team Role")
    private String teamRole;

    @Positive
    @NotNull
    private Integer count;
}
