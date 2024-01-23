package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;
    private final StageValidator stageValidator;

    public StageDto save(StageDto stage) {
        stageValidator.validateStage(stage);
        return stageService.save(stage);
    }
}