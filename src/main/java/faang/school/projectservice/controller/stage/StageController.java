package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.TeamRoleTaskStatusDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @GetMapping("/{id}")
    public StageDto findById(@PathVariable long id) {
        return stageService.findById(id);
    }

    @GetMapping("/project/{id}")
    public List<StageDto> findAll(@PathVariable long id) {
        return stageService.findAllStages(id);
    }

    @PutMapping
    public void update(@RequestBody @Valid StageDto stageDto) {
        stageService.updateStage(stageDto);
    }

    @DeleteMapping("/{id}")
    public void deleteStageOfProject(@PathVariable long id, @RequestBody @Valid StageDto stageDto) {
        stageService.deleteStage(id, stageDto);
    }

    @PostMapping
    public void saveStage(@RequestBody @Valid StageDto stageDto) {
        stageService.save(stageDto);
    }

    @GetMapping("/filter")
    public List<StageDto> findAllWithFilter(@RequestBody TeamRoleTaskStatusDto teamRoleTaskStatusDTO) {
        return stageService.getStagesWithFilters(teamRoleTaskStatusDTO.getTeamRole(), teamRoleTaskStatusDTO.getTaskStatus());
    }

}
