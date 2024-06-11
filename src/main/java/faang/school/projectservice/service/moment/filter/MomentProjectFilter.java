package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.getProjectPattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentStream, MomentFilterDto filterDto) {
        return momentStream.filter(
                moment -> moment.getProjects().stream().anyMatch(
                        project -> project.getName().contains(filterDto.getProjectPattern())
                )
        );
    }
}
