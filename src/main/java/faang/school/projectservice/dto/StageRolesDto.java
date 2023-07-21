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
    @NotNull
    private Long id;
    @NotNull
    private Integer count;
    @NotEmpty
    private String role;
    private Long stageId;
}