package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        Stage savedStage = stageRepository.save(stage);
        return stageMapper.toDto(savedStage);
    }

    public List<StageDto> getStagesByProject(Long projectId, String role, String taskStatus) {
        // Логика фильтрации по ролям и статусу задач
        List<Stage> stages = stageRepository.findByProjectIdAndFilters(projectId, role, taskStatus);
        return stages.stream().map(stageMapper::toDto).collect(Collectors.toList());
    }

    public void deleteStage(Long stageId, String action) {
        Stage stage = stageRepository.getById(stageId);
        // Логика каскадного удаления или переноса задач
        stageRepository.delete(stage);
    }

    public StageDto updateStage(Long stageId, StageDto stageDto) {
        Stage stage = stageRepository.getById(stageId);
        // Логика обновления этапа и участников
        Stage updatedStage = stageRepository.save(stageMapper.toEntity(stageDto));
        return stageMapper.toDto(updatedStage);
    }

    public StageDto getStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        return stageMapper.toDto(stage);
    }
}

