package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.dto.client.stage.StageFilterDTO;
import faang.school.projectservice.service.Stage.StageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping("/{projectId}/{creatorId}")
    public StageDTO create(@RequestBody @NotNull StageDtoCreate stageDtoCreate
            , @PathVariable @NotNull Long projectId, @PathVariable @NotNull Long creatorId) {
        return stageService.create(stageDtoCreate, projectId, creatorId);
    }

    @PutMapping()
    public StageDTO update(@RequestBody StageDTO stageDTO,
                           @RequestParam(required = false) Map<String, String> roleAndCount) {
        return stageService.update(stageDTO, roleAndCount);
    }

    @GetMapping("/filter")
    public List<StageDTO> filter(StageFilterDTO stageFilterDTO) {
        return stageService.getRoleAndStatus(stageFilterDTO);
    }

    @GetMapping("/{projectId}/all")
    public List<StageDTO> getAll(@PathVariable @NotNull Long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/{stageId}")
    public StageDTO get(@PathVariable @NotNull Long stageId) {
        return stageService.getStage(stageId);
    }

    @DeleteMapping("/{stageId}")
    public void delete(@PathVariable @NotNull Long stageId) {
        stageService.deleteCascade(stageId);
    }
}

