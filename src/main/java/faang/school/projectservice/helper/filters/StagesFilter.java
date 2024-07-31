package faang.school.projectservice.helper.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface StagesFilter {

    boolean isApplicable(StageFilterDto stageFilterDto);

    Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto);
}
