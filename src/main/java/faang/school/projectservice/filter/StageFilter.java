package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface StageFilter {
    boolean isApplicable(StageFilterDto filterDto);
    Stream<Stage> apply(Stream<Stage> stageStream, StageFilterDto stageFilterDto);
}
