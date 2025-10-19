package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        log.info("Request to create stage for project id={}", stageDto.projectId());
        return stageService.createStage(stageDto);
    }

    @GetMapping("/by-project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getAllStagesOfProject(@PathVariable Long projectId) {
        log.info("Request to get all stages of project id={}", projectId);
        return stageService.getAllStagesOfProject(projectId);
    }

    @GetMapping("/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public StageDto getStageById(@PathVariable Long stageId) {
        log.info("Request to get stage id={}", stageId);
        return stageService.getById(stageId);
    }

    @PatchMapping("/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public StageDto updateStage(@PathVariable Long stageId, @Valid @RequestBody UpdateStageDto updateStageDto) {
        log.info("Request to update stage id={}", updateStageDto.stageId());
        return stageService.updateStage(stageId, updateStageDto);
    }

    @DeleteMapping("/{stageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStage(@PathVariable Long stageId) {
        log.info("Request to delete stage id={}", stageId);
        stageService.deleteById(stageId);
    }
}