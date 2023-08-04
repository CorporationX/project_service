package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StageController {

    private final StageService stageService;

    @PostMapping("/stage")//поменяю в будущем когда договоримся об общем виде
    public StageDto createProjectStage(@RequestBody StageDto stageDto) {
        return stageService.create(stageDto);
    }

    @GetMapping("/{projectId}/stage")
    public List<StageDto> getAllProjectStages(@PathVariable long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/stage/{stageId}")
    public StageDto getStageById(@PathVariable long stageId) {
        return stageService.getStageById(stageId);
    }
}
