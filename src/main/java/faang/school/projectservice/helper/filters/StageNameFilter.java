package faang.school.projectservice.helper.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

class StageNameFilter implements StagesFilter{
    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getStageName()!= null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> stage.getStageName().equals(stageFilterDto.getStageName()));
    }
}
