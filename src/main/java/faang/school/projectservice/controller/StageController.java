package faang.school.projectservice.controller;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    public void createStage(Stage stage, Project project) {
       stageService.createStage(stage, project);
    }
}
