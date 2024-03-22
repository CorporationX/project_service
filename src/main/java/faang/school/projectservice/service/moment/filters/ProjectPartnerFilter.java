package faang.school.projectservice.service.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
@Component
public class ProjectPartnerFilter implements MomentFilter {
    private List<Project> projects;

    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters.getProjects() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments
                .filter(moment -> moment.getProjects().containsAll(filters.getProjects()));
    }
}
