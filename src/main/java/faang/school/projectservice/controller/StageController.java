package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="Stage", description = "API для управления этапами.")
@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @Operation(
            summary = "Создать этап",
            tags = { "stage", "post" })
    @PostMapping
    public StageDto createStage(@RequestBody StageDto stageDto) {
        validateStage(stageDto);
        return stageService.createStage(stageDto);
    }

    @Operation(
            summary = "Удалить этап",
            tags = { "stage", "delete" })
    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable long stageId) {
        stageService.deleteStage(stageId);
    }

    @Operation(
            summary = "Получить этап по ID",
            tags = { "stage", "get" })
    @GetMapping("/{stageId}")
    public StageDto getStageById(@PathVariable long stageId) {
        return stageService.getStageById(stageId);
    }

    @Operation(
            summary = "Получить этапы проекта",
            tags = { "stage", "get" })
    @GetMapping("/{projectId}")
    public List<StageDto> findAllStagesOfProject(@PathVariable Long projectId) {
        return stageService.findAllStagesOfProject(projectId);
    }

    private void validateStage(StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Stage cannot be null");
        }
        if (stageDto.getProjectId() == null) {
            throw new DataValidationException("The stage NECESSARILY refers to some kind of project!!!");
        }
        if (stageDto.getStageName() == null || stageDto.getStageName().isBlank()) {
            throw new DataValidationException("Stage name cannot be null or blank");
        }
        if (stageDto.getStageRoles() == null || stageDto.getStageRoles().isEmpty()) {
            throw new DataValidationException("Stage roles cannot be null or empty");
        }
    }


}
