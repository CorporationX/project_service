package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.client.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface StageFilter {
    boolean isApplicable(StageFilterDto stageFilterDto);

    Stream<Stage> apply(Stream<Stage> stageStream, StageFilterDto stageFilterDto);
}
