package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;

    private final StageMapper stageMapper;
    private final List<StageFilterDto> stageFilters;

    @Transactional(readOnly = true)
    public List<StageDto> getAllStagesByStatus(StageFilterDto filters) {
        List<Stage> stages = stageRepository.findAll();
        stageFilters.forEach(filter -> filter.apply(stages, filters));      /* нужно придумать фильтр*/
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional
    public void deleteStage(StageDto stageDto) {
        stageRepository.delete(stageMapper.toEntity(stageDto));
    }

    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stages.stream().map(stageMapper::toDto).toList();
    }

    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}