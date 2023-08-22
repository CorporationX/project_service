package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage")
@Slf4j
public class StageController {

    private final StageService service;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto create(@RequestBody StageDto stageDto) {
        validateStage(stageDto);
        log.info("Creating stage: {}", stageDto);
        return service.create(stageDto);
    }

    @GetMapping("/{projectId}/stages")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStages(@PathVariable Long projectId) {
        log.info("Getting stages for project: {}", projectId);
        return service.getStagesByProjectId(projectId);
    }

    private void validateStage(StageDto stageDto) {

    }
}
