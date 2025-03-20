package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.dto.client.stage.StageFilterDTO;
import faang.school.projectservice.service.StageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping("/{projectId}/{creatorId}")
    public StageDTO create(@RequestBody @NotNull StageDtoCreate stageDtoCreate
            , @RequestParam @NotNull Long projectId, @RequestParam @NotNull Long creatorId) {
        return stageService.create(stageDtoCreate, projectId, creatorId);
    }
    @PutMapping()
    public StageDTO update(@RequestBody StageDTO stageDTO){
    return stageService.update(stageDTO);
    }
    @GetMapping("/filter")
    public List<StageDTO> filter(StageFilterDTO stageFilterDTO) {
        return stageService.getRoleAndStatus(stageFilterDTO);
    }
    @GetMapping("/{projectId}/all")
    public List<StageDTO> getAll(@RequestParam @NotNull Long projectId) {
        return stageService.getAllProjectStages(projectId);
    }
    @GetMapping("/{stageId}")
    public List<StageDTO> get(@RequestParam @NotNull Long stageId) {
        return stageService.getStage(stageId);
    }
    @DeleteMapping
    public void delete(@RequestParam @NotNull Long stageId) {
        stageService.delete(stageId);
    }
}

