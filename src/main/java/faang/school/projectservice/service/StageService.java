package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;

    private final StageMapper stageMapper;

    private final List<ProjectStatusFilter> projectStatusFilters;

    public StageDto createStage(StageDto stageDto) {
        validationStageDto(stageDto);
        Stage save = stageRepository.save(stageMapper.toEntity(stageDto));
        return stageMapper.toDto(save);
    }

    public List<StageDto> getStagesByProjectStatus(ProjectStatusFilterDto filter) {
        Stream<Stage> allStages = stageRepository.findAll().stream();
        List<ProjectStatusFilter> projectStatusFiltersIsApplicable = projectStatusFilters.stream()
                .filter(projectStatusFilter -> projectStatusFilter.isApplicable(filter))
                .toList();
        for (ProjectStatusFilter projectStatusFilter : projectStatusFiltersIsApplicable) {
            allStages = projectStatusFilter.apply(allStages, filter);
        }
        return allStages.map(stageMapper::toDto).toList();
    }

    private void validationStageDto(StageDto stageDto) {
        if (stageDto.getProject().getStatus().equals(ProjectStatus.COMPLETED) ||
                stageDto.getProject().getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("You cannot create a stage in a closed or canceled project");
        }

    }

}
