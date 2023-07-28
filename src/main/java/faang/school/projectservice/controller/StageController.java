package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping("/stage")
    public StageDto createStage(@RequestBody StageDto stage) {
        return stageService.createStage(stage);
    }
}
