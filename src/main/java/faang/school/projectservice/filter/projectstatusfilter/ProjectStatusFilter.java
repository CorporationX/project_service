package faang.school.projectservice.filter.projectstatusfilter;

import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface ProjectStatusFilter {

    boolean isApplicable(ProjectStatusFilterDto filter);

    Stream<Stage> apply(Stream<Stage> stageStream, ProjectStatusFilterDto filter);
}
