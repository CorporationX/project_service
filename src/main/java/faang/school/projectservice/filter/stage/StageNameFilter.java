package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

class StageNameFilter implements Filter<StageFilterDto, Stage> {

    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getStageName() != null && !stageFilterDto.getStageName().isBlank();
    }

    @Override
    public Stream<Stage> applyFilter(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> stage.getStageName().contains(stageFilterDto.getStageName()));
    }
}