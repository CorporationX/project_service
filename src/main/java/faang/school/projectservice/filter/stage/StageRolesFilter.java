package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class StageRolesFilter implements Filter<Stage, StageFilterDto> {
    private final StageMapper stageMapper;
    @Override
    public boolean isApplicable (StageFilterDto stageFilterDto) {
        return !(stageFilterDto.getRoleIds() == null || stageFilterDto.getRoleIds().isEmpty());
    }

    @Override
    public Stream<Stage> apply (Stream<Stage> stages, StageFilterDto stageFilterDto) {
        ArrayList<StageDto> stageDtos = new ArrayList<>(stages.map(stageMapper::toDto).toList());
        stageDtos.retainAll(stageFilterDto.getRoleIds());

        return stageDtos.stream().map(stageMapper::toEntity);
    }
}
