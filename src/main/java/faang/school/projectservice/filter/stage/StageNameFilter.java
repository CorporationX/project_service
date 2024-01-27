package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageNameFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filter) {
        return filter.getStageNamePattern() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filter) {
        return stages.filter(stage -> stage.getStageName().contains(filter.getStageNamePattern()));
    }
}