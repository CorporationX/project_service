package faang.school.projectservice.service.project.stage.filters;

import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface StageFilter {
    boolean isApplicable(StageFilterDto filters);

    Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters);
}
