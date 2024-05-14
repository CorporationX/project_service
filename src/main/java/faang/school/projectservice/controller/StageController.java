package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/stage")
@RequiredArgsConstructor
@Validated
public class StageController {
    private final StageService stageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping(path = "/project/{id}")
    public List<StageDto> getAllStagesByProjectIdAndStatus(
            @Positive @PathVariable(name = "id") Long projectId,
            @NotBlank @RequestParam(name = "status") String statusFilter
    ) {
        return stageService.getAllStagesByProjectIdAndStatus(projectId, statusFilter);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StageDto deleteStageById(@Positive @PathVariable(name = "id") Long stageId) {
        return stageService.deleteStageById(stageId);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StageDto updateStage(
            @Positive @PathVariable(name = "id") Long stageId,
            @Valid @RequestBody StageDto stageDto
    ) {
        return stageService.updateStage(stageId, stageDto);
    }


    @GetMapping(path = "/project/{id}")
    public List<StageDto> getAllStagesByProjectId(@Positive @PathVariable(name = "id") Long projectId) {
        return stageService.getAllStagesByProjectId(projectId);
    }

    @GetMapping(path = "/{id}")
    public StageDto getStageById(@Positive @PathVariable(name = "id") Long stageId) {
        return stageService.getStageById(stageId);
    }
}
