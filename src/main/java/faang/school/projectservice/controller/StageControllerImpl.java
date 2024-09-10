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
public class StageControllerImpl implements StageController {
    private final StageService service;
    private final StageControllerValidator validator;

    @Override
    public void create(StageDto stageDto) {
        validator.validateStageDto(stageDto);
        service.create(stageDto);
    }

    @Override
    public List<StageDto> getAllStages(Long projectId) {
        validator.validateId(projectId);
        return service.getAllStages(projectId);
    }

    @Override
    public StageDto getStageById(Long stageId) {
        validator.validateId(stageId);
        return service.getStageById(stageId);
    }

    @Override
    public void deleteStage(StageDto stageDto) {
        service.deleteStage(stageDto);
    }

    @Override
    public List<StageDto> getFilteredStages(Long projectId, StageFilterDto filterDto) {
        validator.validateId(projectId);
        return service.getFilteredStages(projectId, filterDto);
    }

    @Override
    public void update(StageDto dto) {
        validator.validateStageDto(dto);
        service.updateStage(dto);
    }
}
