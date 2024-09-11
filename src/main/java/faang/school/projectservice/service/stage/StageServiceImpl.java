package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.stage.StageValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private List<StageFilter> stageFilters;
    private final StageValidator validator;
    private final StageMapper stageMapper;

    @Override
    public StageDto createStage(@Valid StageDto stageDto) {
        validator.validateProject(stageDto.projectId());

        Stage stage = stageMapper.toEntity(stageDto);

        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Override
    public List<StageDto> getProjectStages(long projectId, StageFilterDto filterDto) {
        Project project = projectRepository.getProjectById(projectId);

        List<Stage> stages = project.getStages();

        List<Stage> stageList = stageFilters
                .stream()
                .filter(stageFilter -> stageFilter.isApplicable(filterDto))
                .reduce(stages.stream(),
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();

        return stageMapper.toStageDtos(stageList);
    }

    @Override
    public void deleteStage(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        //Жду ответа по каскаду
    }

    @Override
    public StageDto updateStage(long stageId) {
        return null;
    }
}
