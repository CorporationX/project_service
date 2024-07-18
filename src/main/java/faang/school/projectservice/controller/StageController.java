package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    // Создание этапа.
    @PostMapping("")
    public ResponseEntity<StageDto> createStage(@RequestBody StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Не переданы данные этапа!");
        }
        return ResponseEntity.ok(stageService.createStage(stageDto));
    }

    // Получить все этапы проекта с фильтром по статусу (в работе, выполнено и т.д).
    @PostMapping("/project/{id}")
    public ResponseEntity<List<StageDto>> getAllStagesFilteredByProjectStatus(
            @PathVariable("id") Long projectId,
            @RequestBody ProjectFilterDto filters) {
        if (projectId == null
                || filters == null) {
            throw new DataValidationException("Введите идентификатор проекта," +
                    " или данные фильтра!");
        }
        return ResponseEntity.ok(stageService
                .getAllStagesFilteredByProjectStatus(projectId, filters));
    }

    // Удалить этап.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable("id") Long id) {
        if (id == null) {
            throw new DataValidationException("Введите идентификатор этапа!");
        }
        stageService.deleteStage(id);
        return ResponseEntity.ok().build();
    }

    // Обновить этап.
    @PutMapping("")
    public ResponseEntity<StageDto> updateStage(@RequestBody StageDto stageDto,
                                                @RequestParam("role") TeamRole teamRole) {
        if (stageDto == null || teamRole == null) {
            throw new DataValidationException("Введите данные этапа!");
        }
        return ResponseEntity.ok(stageService.updateStage(stageDto, teamRole));
    }

    // Получить все этапы проекта.
    @GetMapping("/project/{id}")
    public ResponseEntity<List<StageDto>> getStagesOfProject(@PathVariable("id") Long projectId) {
        if (projectId == null) {
            throw new DataValidationException("Введите идентификатор проекта!");
        }
        return ResponseEntity.ok(stageService.getStagesOfProject(projectId));
    }

    // Получить конкретный этап по Id.
    @GetMapping("/{id}")
    public ResponseEntity<StageDto> getStageById(@PathVariable("id") Long stageId) {
        if (stageId == null) {
            throw new DataValidationException("Введите идентификатор этапа!");
        }
        return ResponseEntity.ok(stageService.getStageById(stageId));
    }
}
