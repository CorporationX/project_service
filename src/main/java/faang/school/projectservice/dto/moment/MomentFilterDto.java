package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MomentFilterDto {
    @Range(min = 1, max = 12, message = "Month must be between 1 and 12")
    private Integer month;
    private List<Long> projectIds;
}
