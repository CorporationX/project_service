package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stages")
@Validated
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@Valid @NotNull StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/projects/{projectId}")
    public List<StageDto> getProjectStages(@PathVariable @Positive long projectId, @Valid @NotNull StageFilterDto filters) {
        return stageService.getProjectStages(projectId, filters);
    }

    @PatchMapping("/{stageId}")
    public StageDto updateStage(@PathVariable @Positive long stageId, @Valid @NotNull StageRolesDto stageRolesDto){
        return stageService.updateStage(stageId, stageRolesDto);
    }

    @GetMapping("/all/projects/{projectId}")
    public List<StageDto> getStages(@PathVariable @Positive long projectId){
        return stageService.getStages(projectId);
    }

    @GetMapping("/{stageId}")
    public StageDto getSpecificStage(@PathVariable @Positive long stageId){
        return stageService.getSpecificStage(stageId);
    }

    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable @Positive long stageId){
        stageService.deleteStage(stageId);
    }
}
