package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class NameSubProjectFilter implements SubProjectFilter{
    @Override
    public boolean isAcceptable(SubProjectDtoFilter goal) {
        return goal.getProjectName()!=null && !goal.getProjectName().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> goal, SubProjectDtoFilter filters) {
        return goal.filter(x->x.getName().contains(filters.getProjectName()));
    }
}
