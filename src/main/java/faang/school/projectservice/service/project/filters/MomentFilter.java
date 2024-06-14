package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filters);

    Stream<Moment> apply(Supplier<Stream<Moment>> moments, MomentFilterDto filters);
}
