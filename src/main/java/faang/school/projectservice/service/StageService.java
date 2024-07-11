package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    // Создание этапа.
    public StageDto createStage(StageDto stageDto) {
        return stageMapper.stageToDto(stageRepository
                .save(stageMapper.stageDtoToEntity(stageDto)));
    }

    // Получить все этапы проекта с фильтром по статусу (в работе, выполнено и т.д).

    // Удалить этап.
    public void deleteStage(Long id) {
        stageRepository.deleteById(id);
    }

    // Обновить этап.

    // Получить все этапы проекта.

    // Получить конкретный этап по Id.
}
