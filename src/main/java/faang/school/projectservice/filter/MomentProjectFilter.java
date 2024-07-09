package faang.school.projectservice.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getProjectFilter() != null;
    }

    @Override
    public Stream<Moment> apply(List<Moment> moments, MomentFilterDto momentFilterDto) {
        return moments.stream()
                .filter(moment -> moment.getProjects().stream()
                        .anyMatch(project -> project.getName().equals(momentFilterDto.getProjectFilter())));
    }
}
