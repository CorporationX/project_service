package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
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
        validateExecutorsAndRoles(stageDto);
        log.info("Creating stage: {}", stageDto);
        return service.create(stageDto);
    }

    @GetMapping("/{projectId}/stages")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStages(@PathVariable Long projectId) {
        log.info("Getting stages for project: {}", projectId);
        return service.getStagesByProjectId(projectId);
    }

    private void validateExecutorsAndRoles(StageDto stageDto) {
        if (stageDto.getTeamMemberIds().size() < stageDto.getStageRoleIds().size()) {
            throw new DataValidationException("Stage roles must not be greater than team members");
        }
    }
}