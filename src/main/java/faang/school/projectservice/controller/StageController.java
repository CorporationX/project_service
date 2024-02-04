package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Управление этапами проекта")
@RestController
@RequestMapping("/project-stages")
@RequiredArgsConstructor
@Slf4j
public class StageController {

    private final StageService stageService;
    private final StageValidator stageValidator;

    @Operation(summary = "Создание этапа",
            description = "Позволяет создать новый этап проекта")
    @PostMapping("/stage")
    public StageDto createStage(@RequestBody StageDto stage) {
        stageValidator.validateStage(stage);
        log.info("Accepted request to create stage for project with id {}", stage.getProjectId());
        return stageService.createStage(stage);
    }

    @Operation(summary = "Выбор всех этапов по фильтру",
            description = "Выборка всех существующих этапов с возможностью применения фильтрации")
    @GetMapping
    public List<StageDto> getAllStageByFilter(@RequestBody StageFilterDto filters) {
        log.info("Accepted request to get stages using filters {}", filters);
        return stageService.getAllStageByFilter(filters);
    }

    @Operation(summary = "Удаление этапа проекта",
            description = "Позволяет удалять этап и все его задачи")
    @DeleteMapping("/{id}")
    public void deleteStageById(@PathVariable("id") @Parameter(description = "Stage ID", example = "1") Long stageId) {
        log.info("Accepted request to delete stage with id {}", stageId);
        stageService.deleteStageById(stageId);
    }

    @Operation(summary = "Выбор этапа",
            description = "Позволяет выбрать конкретный этап по его id")
    @GetMapping("/{id}")
    public StageDto getStageById(@PathVariable("id") @Parameter(description = "Stage ID", example = "1") Long stageId) {
        log.info("Accepted request to get stage with id {}", stageId);
        return stageService.getStagesById(stageId);
    }

    @Operation(summary = "Обновление этапа",
            description = "Проверяет все ли участники назначены на этап, согласно заявленным требованиям, если нет отправляет приглашения подходящим кандидатам")
    @PutMapping("/{id}")
    public StageDto updateStage(@PathVariable("id") @Parameter(description = "Stage ID", example = "1") Long stageId) {
        log.info("Accepted request to update stage with id {}", stageId);
        return stageService.updateStage(stageId);
    }
}