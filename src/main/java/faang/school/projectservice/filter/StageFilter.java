package faang.school.projectservice.filter;

import faang.school.projectservice.model.dto.StageFilterDto;
import faang.school.projectservice.model.entity.Stage;

import java.util.stream.Stream;

public interface StageFilter {

    boolean isApplicable(StageFilterDto filters);

    Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters);
}
