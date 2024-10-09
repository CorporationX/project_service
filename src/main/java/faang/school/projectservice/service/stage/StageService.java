package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
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

    public StageDto createStage(StageCreateDto stageCreateDto) {
        Stage stage = stageMapper.toStageEntity(stageCreateDto);
        stage.setProject(projectService.getProjectById(stageCreateDto.getProjectId()));

        Stage stageNew = stageRepository.save(stage);

        return stageMapper.toStageDto(stageNew);
    }

    public List<StageDto> getStagesByFilters(StageFilterDto stageFilterDto) {
        return stagesFilters.stream()
                .filter(stagesFilter -> stagesFilter.isApplicable(stageFilterDto))
                .reduce(stageRepository.findAll().stream(),
                        (s, f) -> f.applyFilter(s, stageFilterDto),
                        (s1, s2) -> s1)
                .map(stageMapper::toStageDto)
                .toList();
    }

    public void deleteStage(long id) {
        stageRepository.findById(id)
                .ifPresentOrElse(stageRepository::delete,
                        () -> {
                            entityNotFoundException(id);
                        });
    }

    public StageDto updateStage(long id, StageUpdateDto stageUpdateDto) {
        Stage stage1 = stageRepository.findById(id).map(stage -> {
            toEntity(stage, stageUpdateDto);
            return stage;
        }).orElseThrow(() -> {
            entityNotFoundException(id);
            return null;
        });
        return stageMapper.toStageDto(stageRepository.save(stage1));
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

    private Stage toEntity(Stage stage, StageUpdateDto stageUpdateDto) {
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
