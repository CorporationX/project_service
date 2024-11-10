package faang.school.projectservice.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface StageFilter {
    boolean isApplicable(StageFilterDto filters);

    Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters);
}