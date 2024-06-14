package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@Component
public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getProjects() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentStream, MomentFilterDto momentFilterDto) {
        return momentStream.filter(moment -> moment.getProjects()
                .stream()
                .map(Project::getId)
                .collect(Collectors.toSet())
                .containsAll(momentFilterDto.getProjects())
        );
    }
}