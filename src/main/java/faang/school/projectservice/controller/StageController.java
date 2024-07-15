package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDtoForRequest;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Controller
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    public StageDtoForRequest createStage(@Valid StageDtoForRequest stageDto) {
        return stageService.createStage(stageDto);
    }

    public List<StageDtoForRequest> getFilteredStages(StageFilterDto filterDto) {
        return stageService.getFilteredStages(filterDto);
    }

    public void deleteStage(StageDtoForRequest stageDto) {
        stageService.deleteStage(stageDto);
    }

    public StageDtoForRequest updateStage(@Valid StageDtoForRequest stageDto) {
        return null;
    }

    public List<StageDtoForRequest> getAllStages() {
        return stageService.getAllStages();
    }

    public StageDtoForRequest getStage(Long id) {
        return stageService.getStage(id);
    }
}
