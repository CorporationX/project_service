package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageRolesDto {

    @NotEmpty(message = "Team role can not be empty")
    private String teamRole;

    @NotNull(message = "Count can not be null")
    private Integer count;

}