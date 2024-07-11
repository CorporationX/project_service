package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    // Создание этапа.
    public StageDto createStage(StageDto stageDto) {
        if (stageDto == null
                || StringUtils.isBlank(stageDto.getStageName())
                || stageDto.getProject() == null
                || stageDto.getStageRoles().isEmpty()
                || !stageDto.getStageRoles().stream()
                .filter(stageRoles -> stageRoles.getCount() == 0)
                .toList()
                .isEmpty()) {
            throw new DataValidationException("Недостаточно данных для создания этапа!");
        }
        return stageService.createStage(stageDto);
    }

// Получить все этапы проекта с фильтром по статусу (в работе, выполнено и т.д).

    // Удалить этап.
    public void deleteStage(Long id) {
        if (id.equals(null)) {
            throw new DataValidationException("Введите идентификатор этапа!");
        }
//        stageService.
    }

// Обновить этап.

// Получить все этапы проекта.

// Получить конкретный этап по Id.
}
