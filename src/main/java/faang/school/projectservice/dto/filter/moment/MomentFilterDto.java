package faang.school.projectservice.dto.filter.moment;

import faang.school.projectservice.dto.filter.FilterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentFilterDto extends FilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
