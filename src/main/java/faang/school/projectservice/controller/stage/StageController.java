package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@Valid StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/projects/{id}")
    public List<StageDto> getProjectStages(@Positive @PathVariable long projectId, @Valid StageFilterDto filters) {
        return stageService.getProjectStages(projectId, filters);
    }

    @PatchMapping("/{stageId}")
    public StageDto updateStage(@Positive @PathVariable long stageId, @Valid StageRolesDto stageRolesDto){
        return stageService.updateStage(stageId, stageRolesDto);
    }

    @GetMapping("/all/projects/{id}")
    public List<StageDto> getStages(@Positive long projectId){
        return stageService.getStages(projectId);
    }

    @GetMapping("/{id}")
    public StageDto getSpecificStage(@Positive long stageId){
        return stageService.getSpecificStage(stageId);
    }

    @DeleteMapping("/{id}")
    public void deleteStage(@Positive long stageId){
        stageService.deleteStage(stageId);
    }
}
