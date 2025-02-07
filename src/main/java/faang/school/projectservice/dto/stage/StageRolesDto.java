package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String teamRole;
    @NotNull
    @Min(value = 1, message = "The min number of people for each role is one!")
    private int count;

}
