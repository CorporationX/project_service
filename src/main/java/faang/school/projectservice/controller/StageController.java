package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.service.StageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping("/{projectId}/{creatorId}")
    public StageDtoCreate create(@RequestBody StageDtoCreate stageDtoCreate
            , @RequestParam @NotNull Long projectId, @RequestParam @NotNull Long creatorId) {
        return stageService.create(stageDtoCreate, projectId, creatorId);
    }
}
