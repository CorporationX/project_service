package faang.school.projectservice.service.stage.impl;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;

    @Override
    public StageDto createStage(StageDto stageDto) {
        return null;
    }

    @Override
    public List<StageDto> getAllStagesByProjectIdAndStatus(Long projectId, String statusFilter) {
        return null;
    }

    @Override
    public StageDto deleteStageById(Long stageId) {
        return null;
    }

    @Override
    public StageDto updateStage(Long stageId, StageDto stageDto) {
        return null;
    }

    @Override
    public List<StageDto> getAllStagesByProjectId(Long projectId) {
        return null;
    }

    @Override
    public StageDto getStageById(Long stageId) {
        return null;
    }
}
