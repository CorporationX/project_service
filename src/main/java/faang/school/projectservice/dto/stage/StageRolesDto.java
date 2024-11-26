package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class StageRolesDto {

    @NotNull(message = "Team role is required")
    private TeamRole teamRole;

    @NotNull(message = "Count is required")
    @PositiveOrZero(message = "Count must be positive or zero")
    private Integer count;

    @NotBlank(message = "Pattern should not be blank")
    @Size(max = 255, message = "Pattern must not exceed 255 characters")
    private String pattern;
}
