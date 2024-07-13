package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@RequestMapping("/stage")
@Controller
public class StageController {
    private final StageService stageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto addStage(StageDto stageDto) {
        return stageService.create(stageDto);
    }
}