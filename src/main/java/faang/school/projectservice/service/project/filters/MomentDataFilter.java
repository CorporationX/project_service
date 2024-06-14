package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class MomentDataFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return Objects.nonNull(filters.getDate());
    }

    @Override
    public Stream<Moment> apply(Supplier<Stream<Moment>> moments, MomentFilterDto filters) {
        return moments.get().filter(moment ->
                moment.getDate().getMonth().equals(filters.getDate().getMonth()));
    }
}
