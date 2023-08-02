package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageDtoForUpdate;
import faang.school.projectservice.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

import static faang.school.projectservice.validator.StageValidator.validateId;
import static faang.school.projectservice.validator.StageValidator.validateStageDto;
import static faang.school.projectservice.validator.StageValidator.validateStageId;
import static faang.school.projectservice.validator.StageValidator.validateStatus;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
@Tag(name = "Stage", description = "Stages of a project")
public class StageController {
    private final StageService stageService;

    @Operation(summary = "Create stage")
    @PostMapping("/stages")
    public StageDto createStage(@RequestBody StageDto stageDto) {
        validateId(stageDto);
        return stageService.createStage(stageDto);
    }

    @Operation(summary = "Filter stages by status")
    @PostMapping("/filterStatus")
    public List<StageDto> getAllStagesByStatus(@RequestParam(value = "status")
                                               @Parameter(description = "Status of stages", example = "created")
                                               String status) {
        validateStatus(status);
        return stageService.getAllStagesByStatus(status);
    }

    @Operation(summary = "Delete stage")
    @DeleteMapping("/deleteStage/{stageId}")
    public void deleteStage(@PathVariable("stageId")
                            @Parameter(description = "Stage ID", example = "1")
                            Long stageId) {
        validateStageId(stageId);
        stageService.deleteStageById(stageId);
    }

    @Operation(summary = "Update stage")
    @PutMapping("/stage")
    public StageDto updateStage(@RequestBody StageDtoForUpdate stageDto) {
        validateStageDto(stageDto);
        return stageService.updateStage(stageDto);
    }

    @Operation(summary = "Get all stages")
    @GetMapping("/allStages")
    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    @Operation(summary = "Get stage by ID")
    @PostMapping("/stageById/{stageId}")
    public StageDto getStageById(@PathVariable("stageId")
                                 @Parameter(description = "Stage ID", example = "1")
                                 Long stageId) {
        validateStageId(stageId);
        return stageService.getStageById(stageId);
    }
}