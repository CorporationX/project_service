package faang.school.projectservice.filter.moment;


import lombok.NoArgsConstructor;

import java.time.Month;
import java.util.List;

public record MomentFilterDto(Month month, List<Long> projectIds) {}
