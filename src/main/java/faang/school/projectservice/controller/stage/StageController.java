package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StageController {

    private final StageService stageService;

    public StageDto createProjectStage(StageDto stageDto) {
        validateStageName(stageDto);

        return stageService.create(stageDto);
    }

    private void validateStageName(StageDto stageDto) {
        if (stageDto.getStageName() == null || stageDto.getStageName().isBlank()) {
            throw new DataValidationException("Stage name can't be blank or null");
        }
    }
}
