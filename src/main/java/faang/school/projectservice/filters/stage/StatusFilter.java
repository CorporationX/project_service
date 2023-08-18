package faang.school.projectservice.filters.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> entityStream, StageFilterDto filterDto) {
        return entityStream.filter(
                stage -> stage.getStageStatus().equals(filterDto.getStatus())
        );
    }
}
