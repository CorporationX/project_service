package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<StageDto> getAllStagesFilteredByProjectStatus(Long projectId,
                                                           ProjectFilterDto filters) {
        if (projectId == null
        || filters == null) {
            throw new DataValidationException("Введите идентификатор проекта," +
                    " или данные фильтра!");
        }
       return stageService.getAllStagesFilteredByProjectStatus(projectId, filters);
    }

    // Удалить этап.
    public void deleteStage(Long id) {
        if (id == null) {
            throw new DataValidationException("Введите идентификатор этапа!");
        }
        stageService.deleteStage(id);
    }

    // Обновить этап.
    public void updateStage(StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Введите данные этапа!");
        }
        stageService.updateStage(stageDto);
    }

    // Получить все этапы проекта.
    public List<StageDto> getStagesOfProject(Long projectId) {
        if (projectId == null) {
            throw new DataValidationException("Введите идентификатор проекта!");
        }
        return stageService.getStagesOfProject(projectId);
    }

    // Получить конкретный этап по Id.
    public StageDto getStageById(Long stageId) {
        if (stageId == null) {
            throw new DataValidationException("Введите идентификатор этапа!");
        }
        return stageService.getStageById(stageId);
    }
}
