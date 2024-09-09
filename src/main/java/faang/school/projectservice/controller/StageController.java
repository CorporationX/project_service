package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageDto;
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
        validator.validatorStageDto(stageDto);
        service.create(stageDto);
    }

    public List<StageDto> getAllStages(Long projectId) {
        validator.validateId(projectId);
        return service.getAllStages(projectId);
    }

    public StageDto getStageById(Long id) {
        validator.validateId(id);
        return service.getStageById(id);
    }
}
