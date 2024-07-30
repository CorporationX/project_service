package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDeleteTaskStrategyDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.teamrole.TeamRoleDto;
import faang.school.projectservice.filter.stagefilter.StageFilter;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validation.StageValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final List<StageFilter> stageFilters;
    private final StageValidator stageValidator;
    private final StageRolesRepository stageRolesRepository;

    @Transactional
    public Stage createStageEntity(StageDto stageDto) {
        stageValidator.validationProjectById(stageDto.getProjectId());
        Stage stage = stageMapper.toEntity(stageDto);
        List<StageRoles> stageRoles = stage.getStageRoles();
        stage.setStageRoles(null);

        Stage savedStage = stageRepository.save(stage);
        stageRoles.forEach(role -> role.setStage(savedStage));
        stageRolesRepository.saveAll(stageRoles);
        return stage;
    }

    @Transactional
    public StageDto createStage(StageDto stageDto){
        return stageMapper.toDto(createStageEntity(stageDto));
    }

    @Transactional(readOnly = true)
    public List<StageDto> filterStages(Long projectId, StageFilterDto stageFilterDto) {
        stageValidator.validationProjectById(projectId);
        List<Stage> stages = stageValidator.getStages(projectId);
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(stageFilterDto))
                .reduce(stages.stream(), (stream,
                                          filter) -> filter.apply(stream, stageFilterDto),
                        Stream::concat)
                .map(stageMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteStage(Long stageToDeleteId,
                            @NotNull StageDeleteTaskStrategyDto strategyDto,
                            Long newStageId) {
        switch (strategyDto.getStrategy()) {
            case CASCADE_DELETE -> cascadeDelete(stageToDeleteId);
            case CLOSE_TASKS -> closeTasks(stageToDeleteId);
            case MOVE_TASKS -> moveTasks(stageToDeleteId, newStageId);
        }
    }

    private void moveTasks(Long stageToDeleteId,
                           @NotNull Long newStageId) {
        Stage stageNew = stageRepository.getById(newStageId);
        Stage stageOld = stageRepository.getById(stageToDeleteId);
        stageNew.getTasks().addAll(stageOld.getTasks());
        stageRepository.delete(stageOld);

    }

    private void closeTasks(Long stageToDeleteId) {
        Stage stage = stageRepository.getById(stageToDeleteId);
        stage.getTasks().forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        stageRepository.delete(stage);
    }

    private void cascadeDelete(Long stageToDeleteId) {
        Stage stage = stageRepository.getById(stageToDeleteId);
        stage.getTasks().clear();
        taskService.findAllByStage(stage).clear();
        stageRepository.delete(stage);
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
            sendInvitation(stageId, teamRoleDto, role.getCount(), team.size());
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

    private void sendInvitation(Long stageId, TeamRoleDto teamRoleDto, int roleCount, Integer teamSize) {
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

}
