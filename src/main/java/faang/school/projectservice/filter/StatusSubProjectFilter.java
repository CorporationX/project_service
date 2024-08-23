package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusSubProjectFilter implements SubProjectFilter {
    @Override
    public boolean isAcceptable(SubProjectDtoFilter goal) {
        return goal.getProjectStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> goal, SubProjectDtoFilter filters) {
        return goal.filter(x -> x.getStatus().equals(filters.getProjectStatus()));
    }
}
