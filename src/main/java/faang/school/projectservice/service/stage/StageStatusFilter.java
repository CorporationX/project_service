package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public class StageStatusFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<Stage> apply(StageFilterDto filter, Stream<Stage> stages) {
        return stages
                .filter(stage -> stage.getStatus().equals(filter.getStatus()));
    }
}
