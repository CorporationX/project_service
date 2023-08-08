package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MomentFilterDto {
    @Min(value = 1, message = "Month must be greater than 0")
    @Max(value = 12, message = "Month must not be greater than 12")
    private Integer month;
    private List<Long> projectIds;
}
