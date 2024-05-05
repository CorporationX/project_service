package faang.school.projectservice.service.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.stream.Stream;

@Component
public class MomentProjectIdsFilter implements MomentFilter{
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getProjectIds() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentStream, MomentFilterDto momentFilterDto) {
        return momentStream.filter(moment -> new HashSet<>(moment.getProjects().stream().map(Project::getId).toList())
                .containsAll(momentFilterDto.getProjectIds()));
    }
}
