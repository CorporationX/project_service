package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.dto.client.stage.StageFilterDTO;
import faang.school.projectservice.service.Stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping("/{projectId}/{creatorId}")
    public StageDTO create(@RequestBody StageDtoCreate stageDtoCreate,
                           @PathVariable Long projectId,
                           @PathVariable Long creatorId) {
        return stageService.create(stageDtoCreate, projectId, creatorId);
    }

    @PutMapping()
    public StageDTO update(@RequestBody StageDTO stageDTO,
                           @RequestParam(required = false) Map<String, String> roleAndCount) {
        return stageService.update(stageDTO, roleAndCount);
    }

    @GetMapping("/filter")
    public List<StageDTO> filter(@RequestBody StageFilterDTO stageFilterDTO) {
        return stageService.getRoleAndStatus(stageFilterDTO);
    }

    @GetMapping("/{projectId}/all")
    public List<StageDTO> getAll(@PathVariable Long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/{stageId}")
    public StageDTO get(@PathVariable Long stageId) {
        return stageService.getStage(stageId);
    }

    @DeleteMapping("/{stageId}")
    public void delete(@PathVariable Long stageId,
                       @RequestParam String strategy,
                       @RequestParam(required = false) Long targetStageId) {
        stageService.deleteWithStrategy(stageId, strategy, targetStageId);
    }
}

