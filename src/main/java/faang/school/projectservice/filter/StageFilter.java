package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface StageFilter {
    boolean isApplicable(StageFilterDto filter);
    Stream<Stage> apply(Stream<Stage> task, StageFilterDto filter);
}
