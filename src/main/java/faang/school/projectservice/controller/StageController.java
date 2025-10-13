package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.CreateStageDto;
import faang.school.projectservice.dto.client.ProjectIdDto;
import faang.school.projectservice.dto.client.ProjectStagesFilterDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageIdDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @PostMapping("/create")
    public StageDto createStage(@Valid @RequestBody CreateStageDto createStageDto) {
        log.info("Request to create stage for project id={}", createStageDto.projectId());
        return stageService.createStage(createStageDto);
    }

    @PostMapping("/get-all")
    public List<StageDto> getAllStagesOfProject(@Valid @RequestBody ProjectIdDto projectIdDto) {
        log.info("Request to get all stages of project id={}", projectIdDto.projectId());
        return stageService.getAllStagesOfProject(projectIdDto);
    }

    @PostMapping("/get")
    public StageDto getStageById(@Valid @RequestBody StageIdDto stageIdDto) {
        log.info("Request to get stage id={}", stageIdDto.stageId());
        return stageService.getById(stageIdDto);
    }

    @PostMapping("/update")
    public StageDto updateStage(@Valid @RequestBody UpdateStageDto updateStageDto) {
        log.info("Request to update stage id={}", updateStageDto.stageId());
        return stageService.updateStage(updateStageDto);
    }

    @PostMapping("/delete")
    public void deleteStage(@Valid @RequestBody StageIdDto stageIdDto) {
        log.info("Request to delete stage id={}", stageIdDto.stageId());
        stageService.deleteById(stageIdDto);
    }
}