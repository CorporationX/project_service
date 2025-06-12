package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MomentPartnerProjectsFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentDto momentDto) {
        return momentDto.getFilterProjectIds() != null &&
                !momentDto.getFilterProjectIds().isEmpty();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentDto momentDto) {
        final List<Long> projectIdsToFilter = momentDto.getFilterProjectIds();

        return moments.filter(moment -> moment.getProjects().stream()
                .anyMatch(project ->
                        projectIdsToFilter.contains(project.getId())));
    }
}
