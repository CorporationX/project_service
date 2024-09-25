package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/stage")
public class StageController {
    private final StageService service;

    @PutMapping("/create")
    public void create(@RequestBody StageDto stageDto) {
        service.create(stageDto);
    }

    @GetMapping("/stages/{projectId}")
    public List<StageDto> getAllStages(@PathVariable("projectId")
                                       @NotNull Long projectId) {
        return service.getAllStages(projectId);
    }

    @GetMapping("/{id}")
    public StageDto getStageById(@PathVariable("id")
                                 @NotNull Long stageId) {
        return service.getStageById(stageId);
    }


    @DeleteMapping
    public void deleteStage(@RequestBody StageDto stageDto) {
        service.deleteStage(stageDto);
    }

    @GetMapping("/filter/{projectId}")
    public List<StageDto> getFilteredStages(@PathVariable("projectId") @NotNull Long projectId,
                                            @RequestBody StageFilterDto filterDto) {
        return service.getFilteredStages(projectId, filterDto);
    }


    @PutMapping("/update")
    public void update(@RequestBody StageDto dto) {
        service.updateStage(dto);
    }
}
