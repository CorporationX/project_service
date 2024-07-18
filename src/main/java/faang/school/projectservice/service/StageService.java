package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageDeleteTaskStrategyDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageFilterDto;
import faang.school.projectservice.dto.client.TeamRoleDto;
import faang.school.projectservice.filter.stagefilter.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@Slf4j
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final List<StageFilter> stageFilters;
    private final StageRolesMapper stageRolesMapper;

    @Autowired
    public StageService(StageRepository stageRepository,
                        StageMapper stageMapper,
                        ProjectService projectService,
                        TaskService taskService,
                        List<StageFilter> stageFilters,
                        StageRolesMapper stageRolesMapper) {
        this.stageRepository = stageRepository;
        this.stageMapper = stageMapper;
        this.projectService = projectService;
        this.taskService = taskService;
        this.stageFilters = stageFilters;
        this.stageRolesMapper = stageRolesMapper;
    }

    @Transactional
    public void createStage(StageDto stageDto) {
        validationProjectById(stageDto.getProjectId());
        Stage stage = stageMapper.toEntity(stageDto);
        List<StageRoles> rolesList = stageRolesMapper.toEntities(stageDto.getStageRolesDtosList());
        rolesList.forEach(role -> role.setStage(stage));
        stage.setStageRoles(rolesList);
        stageRepository.save(stage);
    }

    @Transactional(readOnly = true)
    public List<StageDto> filterStages(Long projectId, StageFilterDto stageFilterDto) {
        validationProjectById(projectId);
        List<Stage> stages = getValidationStagesIsEmpty(projectId);
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(stageFilterDto))
                .reduce(stages.stream(), (stream,
                                          filter) -> filter.apply(stream, stageFilterDto),
                        Stream::concat)
                .map(stageMapper::toDto)
                .toList();
    }

    @Transactional
    public StageDeleteTaskStrategyDto deleteStage(Long stageToDeleteId,
                                                  StageDeleteTaskStrategyDto strategyDto,
                                                  Long newStageId) {
        switch (strategyDto.getStrategy()) {
            case CASCADE_DELETE -> {
                return cascadeDelete(stageToDeleteId, strategyDto);
            }
            case CLOSE_TASKS -> {
                return closeTasks(stageToDeleteId, strategyDto);
            }
            case MOVE_TASKS -> {
                return moveTasks(stageToDeleteId, strategyDto, newStageId);
            }
            default -> {
                log.info("Unknown strategy in method deleteStage");
                throw new IllegalArgumentException("Unknown strategy: " + strategyDto.getStrategy());
            }
        }
    }

    private StageDeleteTaskStrategyDto moveTasks(Long stageToDeleteId,
                                                 StageDeleteTaskStrategyDto strategyDto,
                                                 Long newStageId) {
        if (newStageId == null) {
            log.info("NewStage in method moveTask is null");
            throw new IllegalArgumentException("newStageId must be provided for MOVE_TASKS strategy");
        }
        stageRepository.getById(newStageId).getTasks().addAll(stageRepository.getById(stageToDeleteId).getTasks());
        stageRepository.delete(stageRepository.getById(stageToDeleteId));
        return strategyDto;
    }

    private StageDeleteTaskStrategyDto closeTasks(Long stageToDeleteId,
                                                  StageDeleteTaskStrategyDto strategyDto) {
        stageRepository.getById(stageToDeleteId).getTasks().forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        stageRepository.delete(stageRepository.getById(stageToDeleteId));
        return strategyDto;
    }

    private StageDeleteTaskStrategyDto cascadeDelete(Long stageToDeleteId,
                                                     StageDeleteTaskStrategyDto strategyDto) {
        stageRepository.getById(stageToDeleteId).getTasks().clear();
        taskService.findAllByStage(stageRepository.getById(stageToDeleteId)).clear();
        stageRepository.delete(stageRepository.getById(stageToDeleteId));
        return strategyDto;
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStageByProjectId(Long projectId) {
        return stageMapper.toDtos(projectService.getProjectById(projectId).getStages());
    }

    @Transactional(readOnly = true)
    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    @Transactional
    public void updateStageWithTeamMembers(Long stageId, TeamRoleDto teamRoleDto) {
        StageRoles role = getStageRoles(stageId, teamRoleDto);
        List<TeamMember> team = getTeamMembers(stageId, teamRoleDto);
        if (role.getCount() > team.size()) {
            sentInvitation(stageId, teamRoleDto,role.getCount(), team.size());
        }
    }

    private List<TeamMember> getTeamMembers(Long stageId, TeamRoleDto teamRoleDto) {
        return stageRepository.getById(stageId).getExecutors()
                .stream()
                .filter(teamMember -> teamMember.getRoles()
                        .stream()
                        .anyMatch(specialization -> specialization.equals(teamRoleDto.getRolePattern())))
                .toList();
    }

    private StageRoles getStageRoles(Long stageId, TeamRoleDto teamRoleDto) {
        return stageRepository.getById(stageId).getStageRoles()
                .stream()
                .filter(filter -> filter.getTeamRole()
                        .equals(teamRoleDto.getRolePattern()))
                .findFirst()
                .orElseThrow(() -> {
                    log.info("Method getStageRoles: Role not found for given pattern");
                    return new NoSuchElementException("Role not found for the given pattern");
                });
    }


    private void sentInvitation(Long stageId, TeamRoleDto teamRoleDto, int roleCount, Integer teamSize) {
        projectService.getProjectById(stageRepository.getById(stageId)
                        .getProject().getId()).getTeams()
                .stream()
                .flatMap(teams -> teams.getTeamMembers()
                        .stream()
                        .filter(teamMember -> teamMember.getStages()
                                .stream()
                                .noneMatch(stage -> stage.equals(stageRepository.getById(stageId))))
                        .filter(teamMember -> teamMember.getRoles()
                                .stream()
                                .anyMatch(specialization -> specialization.equals(teamRoleDto.getRolePattern()))))
                .limit(roleCount - teamSize)
                .forEach(teamMember -> System.out.println("Glad to see you in our team dude!"));
    }

    private void validationProjectById(Long projectId){
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            log.info("Method getValidationProjectById return Null");
            throw new RuntimeException("Project is null");
        }
    }


    private List<Stage> getValidationStagesIsEmpty(Long projectId){
        List<Stage> stages = projectService.getProjectById(projectId).getStages();
        if (stages.isEmpty()){
            log.info("Method getValidationStageIsEmpty return empty List<Stage>");
            throw new RuntimeException("List is empty");
        } else {
            return stages;
        }
    }

}
