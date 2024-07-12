package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MomentProjectFilter implements MomentFilter{
    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return Objects.nonNull(filters.getProjects());
    }

    @Override
    public Stream<Moment> apply(Supplier<Stream<Moment>> moments, MomentFilterDto filters) {
        return moments.get().filter(moment -> moment.getProjects().stream()
                .anyMatch(project -> filters.getProjects().contains(project.getId())));
    }
}
