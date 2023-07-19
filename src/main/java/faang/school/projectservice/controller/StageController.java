package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    public void createStage(StageDto stageDto){
        stageService.createStage(stageDto);
    }

    public List<StageDto> getAllStagesByStatus(StageFilterDto stageFilterDto) {
        return stageService.getAllStagesByStatus(stageFilterDto);
    }

    public void deleteStage(StageDto stageDto){
        stageService.deleteStage(stageDto);
    }

    public List<StageDto> getAllStages(){
        return stageService.getAllStages();
    }

    public StageDto getStageById(Long stageId){
        return stageService.getStageById(stageId);
    }
}