package faang.school.projectservice.dto.moment.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto {
    private Integer month;
    private List<Long> projectIds;
}