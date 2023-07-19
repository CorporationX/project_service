package faang.school.projectservice.filter.projectstatusfilter;

import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilterImpl implements ProjectStatusFilter {
    @Override
    public boolean isApplicable(ProjectStatusFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stageStream, ProjectStatusFilterDto filter) {
        return stageStream.filter(stage -> stage.getProject().getStatus().equals(filter.getStatus()));
    }
}
