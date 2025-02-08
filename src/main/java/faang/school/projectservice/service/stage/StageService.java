package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final List<StageFilter> stageFilter;
    private final StageMapper stageMapper;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage = stageRepository.save(stage);
        return stageMapper.toDto(stage);
    }

    @Transactional
    public List<StageDto> getAllStagesByRole(StageFilter stageFilter, Long userId) {

    }


    @Transactional
    public StageDto deleteStage() {

    }

    @Transactional
    public StageDto updateStage(StageDto stageDto) {
        Stage stage = findAndCheckStageId(stageDto);
        stageMapper.updateEntity(stageDto, stage);
        return stageMapper.toDto(stage);
    }

    public StageDto getAllStages() {

    }

    @Transactional
    public StageDto getStageById(Long stageId) throws EntityNotFoundException {
        return stageRepository.findById(stageId)
                .map(stageMapper::toDto).orElseThrow(
                        () -> new EntityNotFoundException("Stage with " + stageId + " id not found")
                );
    }

    private Stage findAndCheckStageId(StageDto stageDto) {
        return stageRepository.findById(stageDto.stageId()).orElseThrow(
                () -> new EntityNotFoundException("Stage with " + stageDto.stageId() + " id not found")
        );
    }

}
