package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StageController {

    private final StageService stageService;

    public StageDto createProjectStage(StageDto stageDto){
        return stageService.create(stageDto);
    }
}
