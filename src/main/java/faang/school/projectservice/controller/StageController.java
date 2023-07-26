package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    public StageDto createStage(StageDto stageDto) {
        return stageService.createStage(stageDto);
    }
}
