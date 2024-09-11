package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping("/stages")
    public StageDto createStage(@Valid StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/stages")
    public List<StageDto> getProjectStages(@Positive long projectId, @Valid StageFilterDto filters) {
        return stageService.getProjectStages(projectId, filters);
    }
}
