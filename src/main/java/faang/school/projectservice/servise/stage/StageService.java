package faang.school.projectservice.servise.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.helper.exeption.NotNullProvidedException;
import faang.school.projectservice.helper.exeption.StageNotFoundException;
import faang.school.projectservice.helper.filters.StagesFilter;
import faang.school.projectservice.helper.mapper.StageMapper;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRolesRepository stageRolesRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final List<StagesFilter> stagesFilters;

    public void createStage(StageDto stageDto) {
        stageRepository.save(toEntity(stageDto));
    }

    // Вот тут логично было бы возвращать этап с полноценными листами как в методе вывести все этапы,
    // а не листы с ИД, рассудите, поправлю или тут или в методе вывести все этапы
    public List<StageDto> getStagesByFilters(StageFilterDto stageFilterDto) {
        Stream<Stage> stages = stageRepository.findAll().stream();
        return stagesFilters.stream()
                .filter(stagesFilter -> stagesFilter.isApplicable(stageFilterDto))
                .flatMap(stagesFilter -> stagesFilter.apply(stages, stageFilterDto))
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(long id) {
        Optional<Stage> stage = stageRepository.findById(id);
        stage.ifPresentOrElse(stageRepository::delete,
                () -> {
                    throw new NotNullProvidedException();
                });
    }

    public void updateStage(StageDto stageDto) {
        Optional<Stage> stage = stageRepository.findById(stageDto.getStageId());
        stage.ifPresentOrElse(s -> stageRepository.save(toEntity(stageDto)),
                () -> {
                    throw new StageNotFoundException();
                });

    }

    public List<StageDto> getAllStage() {
        return stageMapper.toDtos(stageRepository.findAll());
    }

    public StageDto getStageById(long id) {
        return stageRepository.findById(id)
                .map(stage -> stageMapper.toDto(stageRepository.findById(id).get()))
                .orElseThrow(StageNotFoundException::new);
    }

    private Stage toEntity(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        Optional<List<StageRoles>> stageRoles =
                Optional.of(stageRolesRepository.findAllById(stageDto.getStageRoleIds()));
        Optional<List<Task>> tasks = Optional.of(taskRepository.findAllById(stageDto.getTaskIds()));
        Optional<List<TeamMember>> teamMembers =
                Optional.of(teamMemberRepository.findAllById(stageDto.getExecutorIds()));
        Stage stage = stageMapper.toEntity(stageDto);

        stage.setProject(project);
        stageRoles.ifPresent(stage::setStageRoles);
        tasks.ifPresent(stage::setTasks);
        teamMembers.ifPresent(stage::setExecutors);
        return stage;
    }

}
