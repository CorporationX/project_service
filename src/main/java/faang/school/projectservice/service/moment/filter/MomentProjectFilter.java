package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@Component
public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getProjects() != null && !momentFilterDto.getProjects().isEmpty();
    }

    @Override
    public Stream<Moment> apply(List<Moment> momentList, MomentFilterDto momentFilterDto) {
        return momentList.stream().filter(moment ->
                moment.getProjects()
                        .stream()
                        .map(Project::getId)
                        .collect(Collectors.toSet())
                        .containsAll(momentFilterDto.getProjects())
        );
    }
}