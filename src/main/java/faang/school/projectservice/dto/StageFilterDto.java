package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StageFilterDto {
    @NotEmpty(message = "Status does not empty")
    private String status;
}