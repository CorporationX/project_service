package faang.school.projectservice.filter.moment;


import java.time.Month;
import java.util.List;

public record MomentFilterDto(Month month, List<Long> projectIds) {}
