package faang.school.projectservice.controller;

import faang.school.projectservice.controller.validation.StageValidator;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class StageController {
    private final StageService stageService;
    private final StageValidator stageValidator;

    @PostMapping("/stage")
    public StageDto createStage(@RequestBody StageDto stageDto) {
        StageValidator.validateCreation(stageDto);
        return stageService.createStage(stageDto);
    }
}
