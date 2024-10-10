package faang.school.projectservice.model.filter.stage;

import faang.school.projectservice.model.dto.stage.StageFilterDto;
import faang.school.projectservice.model.entity.stage.Stage;

import java.util.stream.Stream;

public interface StageFilter {
    boolean isApplicable(StageFilterDto filters);

    Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters);
}
