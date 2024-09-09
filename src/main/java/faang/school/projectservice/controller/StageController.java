package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageController {
    private final StageService service;
    private final StageControllerValidator validator;
    public void create(StageDto stageDto) {
        validator.validateStageDto(stageDto);
        service.create(stageDto);
    }

    public List<StageDto> getAllStages(Long projectId) {
        validator.validateId(projectId);
        return service.getAllStages(projectId);
    }

    public StageDto getStageById(Long stageId) {
        validator.validateId(stageId);
        return service.getStageById(stageId);
    }

    public void deleteStage(StageDto stageDto) {
        service.deleteStage(stageDto);
    }

    public List<StageDto> getFilteredStages(Long projectId, StageFilterDto filterDto) {
        validator.validateId(projectId);
        return service.getFilteredStages(projectId, filterDto);
    }

    public void update(StageDto dto) {
        validator.validateStageDto(dto);
        service.updateStage(dto);
    }
}
