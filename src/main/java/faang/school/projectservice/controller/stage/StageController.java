package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.helper.validator.DataValidation;
import faang.school.projectservice.servise.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;
    private final DataValidation dataValidation;


    public void createStage(StageDto stageDto) {
        validCreate(stageDto);
        stageService.createStage(stageDto);
    }

    public List<StageDto> getStagesByFilters(StageFilterDto stageFilterDto) {
        dataValidation.checkOneTypeToNull(stageFilterDto);
        return stageService.getStagesByFilters(stageFilterDto);
    }

    public void deleteStage(long id) {
        dataValidation.checkOneTypeToNull(id);
        stageService.deleteStage(id);
    }

    public void updateStage(StageDto stageDto) {
        dataValidation.checkOneTypeToNull(stageDto);
        stageService.updateStage(stageDto);
    }

    public List<StageDto> getAllStage() {
        return stageService.getAllStage();
    }

    public StageDto getStageById(long id) {
        dataValidation.checkOneTypeToNull(id);
        return stageService.getStageById(id);
    }

    private void validCreate(StageDto stageDto) {
        dataValidation.checkOneTypeToNull(stageDto);
        dataValidation.checkOneTypeToNull(stageDto.getStageRoleIds());
        dataValidation.checkOneTypeToNull(stageDto.getExecutorIds());
        dataValidation.checkOneTypeToNull(stageDto.getTaskIds());
        dataValidation.checkOneTypeToNull(stageDto.getProjectId());
    }
}
