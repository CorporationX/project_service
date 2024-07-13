package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface StageFilter {
    boolean isApplicable(StageFilterDto filter);

    Stream<Stage> apply(StageFilterDto filter, Stream<Stage> stages);
}