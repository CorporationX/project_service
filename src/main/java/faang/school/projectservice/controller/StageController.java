package faang.school.projectservice.controller;

import faang.school.projectservice.controller.validation.StageValidator;
import faang.school.projectservice.dto.project.StageDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class StageController {
    private final StageValidator stageValidator;


    @PostMapping("/stage")
    public StageDto createStage(StageDto stageDto) {
        StageValidator.validateCreation(stageDto);

        return stageDto;
    }
}
