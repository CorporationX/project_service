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

//  Создание этапа. Этап ОБЯЗАТЕЛЬНО относится к какому-то проекту.
//  Все этапы хранятся в базе данных, названия этапов в БД могут повторяться.
//  При создании этапа необходимо определить список ролей и количество человек для каждой роли,
//  которые гарантированно будут задействованы на этапе.

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
//        stageRepository.delete
    }

    @Transactional
    public StageDto updateStage(StageDto stageDto) {
        Stage stage = stageRepository.findById(stageDto.stageId()).orElseThrow(() -> new IllegalArgumentException(""));
        return null;
    }

    public StageDto getAllStages() {

    }

    @Transactional
    public StageDto getStageById(Long stageId) throws EntityNotFoundException {
        return stageRepository.findById(stageId)
                .map(stageMapper::toDto).orElseThrow(
                        () -> new EntityNotFoundException("Stage with " + stageId + " id not found"));
    }

}
