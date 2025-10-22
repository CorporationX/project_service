package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.AllStageFilterDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.service.StageServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stage")
@RequiredArgsConstructor
@Validated
public class StageController {
    private final StageServiceImpl stageServiceImpl;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createStage(@RequestBody @Valid StageCreateDto stageCreateDto) {
        stageServiceImpl.createStage(stageCreateDto);
    }

    @GetMapping("/filter-stages")
    public List<StageDto> getAllStageByFilter(@Valid @RequestBody AllStageFilterDto allStageFilterDto) {
        return stageServiceImpl.getAllStageByFilter(allStageFilterDto);
    }

    @DeleteMapping("/{stageId}/{projectId}")
    public void deleteStage(@PathVariable @NotNull (message = "Specify stage")
                            Long stageId,
                            @NotNull(message = "Specify project")
                            @PathVariable Long projectId) {

        stageServiceImpl.deleteStage(projectId, stageId);
    }

    @PutMapping("/{stageId}")
    public StageDto updateStage(@Valid @RequestBody StageUpdateDto stageUpdateDto,
                                @PathVariable @NotNull Long stageId) {
        return stageServiceImpl.updateStage(stageUpdateDto, stageId);
    }

    @GetMapping("/project/{projectId}/stages")
    public List<StageDto> getStages(@PathVariable @NotNull Long projectId) {
        return stageServiceImpl.getStages(projectId);
    }

    @GetMapping("/project/{stageId}/stage")
    public StageDto getStage(@PathVariable @NotNull Long stageId) {
        return stageServiceImpl.getStage(stageId);
    }

}