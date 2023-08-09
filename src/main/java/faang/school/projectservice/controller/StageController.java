package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "Stage", description = "Stages of a project")
@Validated
@Slf4j
public class StageController {
    private final StageService stageService;

    @Operation(summary = "Create stage")
    @PostMapping()
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        log.debug("Received request to create stage: {}", stageDto);
        return stageService.createStage(stageDto);
    }

    @Operation(summary = "Filter stages by status")
    @GetMapping("/filterStatus")
    public List<StageDto> getAllStagesByStatus(@RequestParam(value = "status")
                                               @Parameter(description = "Status of stages", example = "created")
                                               @NotEmpty String status) {
        log.debug("Received request to filter stage by status: {}", status);
        return stageService.getAllStagesByStatus(status);
    }

    @Operation(summary = "Delete stage")
    @DeleteMapping("/delete/{stageId}")
    public void deleteStage(@PathVariable
                            @Parameter(description = "Stage ID", example = "1")
                            @NotNull Long stageId) {
        log.debug("Received request to delete stage by ID: {}", stageId);
        stageService.deleteStageById(stageId);
    }

    @Operation(summary = "Update stage")
    @PutMapping("/stage")
    public StageDto updateStage(@Valid @RequestBody StageDto stageDto,
                                @RequestParam(value = "stageId") @NotNull Long stageId,
                                @RequestParam(value = "authorId") @NotNull Long authorId) {
        log.debug("Received request to update stage: {} + stageId: {} + authorId: {}", stageDto, stageId, authorId);
        return stageService.updateStage(stageDto, stageId, authorId);
    }

    @Operation(summary = "Get all stages")
    @GetMapping("/list")
    public List<StageDto> getAllStages() {
        log.debug("Received request to get all stages");
        return stageService.getAllStages();
    }

    @Operation(summary = "Get stage by ID")
    @GetMapping("/Id/{stageId}")
    public StageDto getStageById(@Parameter(description = "Stage ID", example = "1")
                                 @PathVariable @NotNull Long stageId) {
        log.debug("Received request to get stage by ID: {}", stageId);
        return stageService.getStageById(stageId);
    }
}