package faang.school.projectservice.contoller.project.stage;

import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.dto.project.stage.StageUpdateDto;
import faang.school.projectservice.serivce.project.stage.StageService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping("/stages")
    public StageDto createStage(@RequestBody StageCreateDto stageCreateDto) {
        return stageService.createStage(stageCreateDto);
    }

    @GetMapping("/stages")
    public List<StageDto> getStages(@Positive @PathVariable Long projectId,
                                    @RequestBody StageFilterDto stageFilterDto) {
        return stageService.getStages(projectId, stageFilterDto);
    }

    @DeleteMapping("/stages/{stageId}")
    public StageDto deleteStage(
            @Positive @PathVariable Long stageId,
            @NotNull RemoveTypeDto removeTypeDto) {
        return stageService.removeStage(stageId, removeTypeDto);
    }

    @PostMapping("/stages")
    public StageDto updateStage(@RequestBody StageUpdateDto stageUpdateDto,
                                @RequestHeader("x-user-id") Long userId) {
        return stageService.updateStage(stageUpdateDto, userId);
    }

    @GetMapping("/stages/{stageId}")
    public StageDto getStage(@Positive @PathVariable Long stageId) {
        return stageService.getStage(stageId);
    }
}
