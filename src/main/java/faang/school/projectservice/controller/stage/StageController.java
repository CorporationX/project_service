package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage.StageService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StageController {

    private final StageService stageService;

    @PostMapping("/project")//поменяю когда договоримся о формате, в остальных тоже
    public StageDto createProjectStage(StageDto stageDto) {
        validateStageName(stageDto);

        return stageService.create(stageDto);
    }

    @GetMapping("/project/{projectId}")
    public List<StageDto> getAllProjectStages(@PathVariable long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/stage")
    public StageDto getStageById(long stageId) {
        return stageService.getStageById(stageId);
    }

    private void validateStageName(StageDto stageDto) {
        if (StringUtils.isBlank(stageDto.getStageName())) {
            throw new DataValidationException("Stage name can't be blank or null");
        }
    }
}
