package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Component
public class MomentFilterProjects implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.projectIds() != null;
    }

    @Override
    public Stream<Moment> apply(MomentFilterDto filterDto, Stream<Moment> moments) {
        return moments.filter(moment -> {
                    List<Long> projectIds = moment.getProjects().stream()
                            .map(Project::getId)
                            .toList();
                    return new HashSet<>(projectIds).containsAll(filterDto.projectIds());
                });
    }
}
