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
    private Long id;

    @NotEmpty
    private String teamRole;

    @NotNull
    private Integer count;

    private Long stageId;
}