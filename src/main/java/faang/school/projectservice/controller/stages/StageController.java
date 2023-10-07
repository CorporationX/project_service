package faang.school.projectservice.controller.stages;

import faang.school.projectservice.dto.stages.StageDto;
import faang.school.projectservice.service.stages.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage")
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto create(@Valid @RequestBody StageDto stageDto) {
        return stageService.create(stageDto);
    }
}
