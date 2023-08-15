package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage")
@Slf4j
public class StageController {

    private final StageService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto create(StageDto stageDto) {
        validateStage(stageDto);
        log.info("Creating stage: {}", stageDto);
        return service.create(stageDto);
    }

    private void validateStage(StageDto stageDto) {
        if (stageDto.getStageId() != null) {
            throw new IllegalArgumentException("Stage must have id");
        }
        if (stageDto.getProjectId() == null) {
            throw new IllegalArgumentException("Stage must have project");
        }
        if (stageDto.getStageName() == null || stageDto.getStageName().isEmpty()) {
            throw new IllegalArgumentException("Stage name must not be empty");
        }
        if (stageDto.getStageRoleIds() == null || stageDto.getStageRoleIds().isEmpty()) {
            throw new IllegalArgumentException("Stage must have at least one role");
        }
        if (stageDto.getTeamMemberIds() == null || stageDto.getTeamMemberIds().isEmpty()) {
            throw new IllegalArgumentException("Stage must have at least one team member");
        }
    }
}
