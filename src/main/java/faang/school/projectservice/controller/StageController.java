package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageRequestAllStageDto;
import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.dto.stage.StageRequestDeleteDto;
import faang.school.projectservice.dto.stage.StageRequestUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.StageServiceImpl;
import jakarta.validation.Valid;
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
    public void createStage(@RequestBody @Valid StageRequestCreateDto stageRequestCreateDto) {
        stageServiceImpl.createStage(stageRequestCreateDto);
    }

    @GetMapping("/filter-stages")
    public List<Stage> getAllStageByFilter(@Valid @RequestBody StageRequestAllStageDto stageRequestAllStageDto) {
        return stageServiceImpl.getAllStageByFilter(stageRequestAllStageDto);
    }


    @DeleteMapping
    public void deleteStage(@Valid StageRequestDeleteDto stageRequestDeleteDto) {
        stageServiceImpl.deleteStage(stageRequestDeleteDto);
    }

    @PutMapping
    public void updateStage(@Valid @RequestBody StageRequestUpdateDto stageRequestUpdateDto) {
        stageServiceImpl.updateStage(stageRequestUpdateDto);
    }

    @GetMapping("/id-stages/{projectId}")
    public List<Stage> getAllStage(@PathVariable Long projectId) {
        validateOrThrow(projectId);
        return stageServiceImpl.getAllStage(projectId);
    }

    @GetMapping("/{stageId}")
    public Stage getStageById(@PathVariable Long stageId) {
        validateOrThrow(stageId);
        return stageServiceImpl.getStageById(stageId);
    }

    private void validateOrThrow(Long id) {
        if (id == null) {
            throw new DataValidationException("Id - не может быть пустым!");
        }
    }
}