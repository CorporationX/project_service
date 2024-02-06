package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.PENDING;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectValidator projectValidator;
    private final StageValidator stageValidator;
    private final List<StageFilter> stageFilters;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StageInvitationRepository stageInvitationRepository;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Long projectId = stageDto.getProjectId();
        projectValidator.existsById(projectId);
        stageValidator.validateStatusProject(projectId);
        Stage stage = stageMapper.toEntity(stageDto);
        stage.getStageRoles().forEach((role) -> role.setStage(stage));
        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Transactional
    public List<StageDto> getAllStageByFilter(StageFilterDto filters) {
        List<Stage> stages = stageRepository.findAll();
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(stages.stream(), filters))
                .distinct()
                .map((stageMapper::toDto))
                .toList();
    }

    @Transactional
    public void deleteStageById(Long stageId) {
        //Удаление делаю именно по объекту, предварительно его получаю через getById, внутри которого выполняется проверка,
        // что такой id есть в бд
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = taskRepository.findAll();
        List<Task> taskListOfStage = tasks.stream()
                .filter((task) -> task.getStage().getStageId().equals(stageId))
                .toList();
        if (!taskListOfStage.isEmpty()) {
            taskRepository.deleteAll(taskListOfStage);
        }
        stageRepository.delete(stage);
    }

    @Transactional
    public StageDto getStagesById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    @Transactional
    public StageDto updateStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<StageRoles> stageRoles = stage.getStageRoles(); // требования к участникам по всем ролям проекта
        List<TeamMember> teamMembers = stage.getExecutors(); // участники этапа
        Map<TeamRole, Long> missedMembers = getMissedMembers(stageRoles, teamMembers); //определение нехватки участников для каждой роли этапа
        if (!missedMembers.isEmpty()) {
            inviteMember(stage, missedMembers);
        }
        return stageMapper.toDto(stageRepository.getById(stage.getStageId()));
    }

    private Map<TeamRole, Long> getMissedMembers(List<StageRoles> stageRoles, List<TeamMember> teamMembers) {
        Map<TeamRole, Long> missedMembers = new HashMap<>();
        stageRoles.forEach((stageRole) -> {
                    TeamRole teamRole = stageRole.getTeamRole();
                    long countMemberForRole = teamMembers.stream()
                            .filter((member) -> member.getRoles().contains(teamRole))
                            .count();
                    long deltaMembers = stageRole.getCount() - countMemberForRole;
                    if (deltaMembers > 0) {
                        missedMembers.put(teamRole, deltaMembers); //пишем в мапу нехватку участников по каждой роли.
                    }
                }
        );
        return missedMembers;
    }

    private void inviteMember(Stage stage, Map<TeamRole, Long> missedMembers) {
        Project project = projectRepository.getProjectById(stage.getProject().getId());
        List<Team> teamsProject = project.getTeams();

        //формируем список участников проекта, которые не участвуют в нашем этапе:
        List<TeamMember> members = teamsProject.stream()
                .flatMap((team -> team.getTeamMembers().stream()
                        .filter((member) -> !member.getStages().contains(stage))))
                .toList();

        missedMembers.entrySet().forEach((missedMember) -> {
            members.stream()
                    .filter(member -> member.getRoles().contains(missedMember.getKey()))
                    .limit(missedMember.getValue())
                    .forEach(member -> sendInvite(member, stage)); //отправка приглашения
        });
    }

    private void sendInvite(TeamMember member, Stage stage) {
        StageInvitation stageInvitation = StageInvitation.builder()
                .description("Вам отправлено приглашение принять участие в этапе " +
                        stage.getStageName() + " на проекте - " + stage.getProject().getName())
                .invited(member)
                .stage(stage)
                .status(PENDING).build();
        stageInvitationRepository.save(stageInvitation);
    }
}