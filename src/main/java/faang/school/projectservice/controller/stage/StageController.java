package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage.StageService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StageController {

    private final StageService stageService;

    @PostMapping("/stage")//поменяю в будущем когда договоримся об общем виде
    public StageDto createProjectStage(@RequestBody StageDto stageDto) {
        validateStageName(stageDto);

        return stageService.create(stageDto);
    }

    private void validateStageName(StageDto stageDto) {
        if (StringUtils.isBlank(stageDto.getStageName())) {
            throw new DataValidationException("Stage name can't be blank or null");
        }
    }
}
