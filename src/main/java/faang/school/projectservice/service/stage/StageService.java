package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.stageroles.StageRolesService;
import faang.school.projectservice.service.task.TaskService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberService teamMemberService;
    private final StageRolesService stageRolesService;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final StageMapper stageMapper;
    private final List<Filter<StageFilterDto, Stage>> stagesFilters;

    public StageCreateDto createStage(StageCreateDto stageCreateDto) {
        Stage stage = stageRepository.save(stageMapper.toStageCreateEntity(stageCreateDto));
        stage.setProject(projectService.getProjectById(stageCreateDto.getProjectId()));

        return stageMapper.toStageCreateDto(stage);
    }

    public List<StageDto> getStagesByFilters(StageFilterDto stageFilterDto) {
        Stream<Stage> stages = stageRepository.findAll().stream();
        return stagesFilters.stream()
                .filter(stagesFilter -> stagesFilter.isApplicable(stageFilterDto))
                .flatMap(stagesFilter -> stagesFilter.applyFilter(stages, stageFilterDto))
                .map(stageMapper::toStageDto)
                .toList();
    }

    public void deleteStage(long id) {
        Optional<Stage> stage = stageRepository.findById(id);
        stage.ifPresentOrElse(stageRepository::delete,
                () -> {
                    entityNotFoundException(id);
                });
    }

    public StageUpdateDto updateStage(StageUpdateDto stageUpdateDto) {
        Optional<Stage> stage = stageRepository.findById(stageUpdateDto.getStageId());

        return stage.map(s -> stageMapper.toStageUpdateDto(stageRepository.save(toUpdateEntity(stageUpdateDto))))
                .orElseThrow(() -> {
                    entityNotFoundException(stageUpdateDto.getStageId());
                    return null;
                });
    }

    public List<StageDto> getAllStage() {
        return stageMapper.toStageDtos(stageRepository.findAll());
    }

    public StageDto getStageById(long id) {
        return stageRepository.findById(id)
                .map(stageMapper::toStageDto)
                .orElseThrow(() -> {
                    entityNotFoundException(id);
                    return null;
                });
    }

    private Stage toUpdateEntity(StageUpdateDto stageUpdateDto) {
        Stage stage = stageMapper.toStageUpdateEntity(stageUpdateDto);

        stage.setProject(projectService.getProjectById(stageUpdateDto.getProjectId()));
        stage.setStageRoles(stageRolesService.getAllById(stageUpdateDto.getStageRoleIds()));
        stage.setTasks(taskService.getAllById(stageUpdateDto.getTaskIds()));
        stage.setExecutors(teamMemberService.getAllById(stageUpdateDto.getExecutorIds()));

        return stage;
    }

    private void entityNotFoundException(long id) {
        throw new EntityNotFoundException("No such request was found " + id);
    }

}
