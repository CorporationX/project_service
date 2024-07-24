package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.StageException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.FateOfTasksAfterDelete;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationRepository stageInvitationRepository;
    private final TaskRepository taskRepository;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;
    private final List<StageFilter> filters;

    public StageDto createStage(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        ProjectStatus currentStatus = project.getStatus();
        validatedProjectStatus(currentStatus);

        Stage stage = stageMapper.toEntity(stageDto);
        stage.setProject(project);
        List<TeamMember> executors = stageDto.getExecutorIds()
                .stream()
                .map(teamMemberRepository::findById)
                .toList();
        stage.setExecutors(executors);

        executors.forEach(teamMember -> {
            if (teamMember.getRoles() == null) {
                throw new StageException("Team member with id: " + teamMember.getId() + " has no role");
            }
        });

        List<StageRoles> stageRolesList = stageRolesMapper.toEntityList(stageDto.getStageRoles());
        stageRolesList.forEach(stageRoles -> stageRoles.setStage(stage));
        stage.setStageRoles(stageRolesList);
        Stage saveStage = stageRepository.save(stage);
        return stageMapper.toDto(saveStage);
    }

    private static void validatedProjectStatus(ProjectStatus currentStatus) {
        if (currentStatus.equals(ProjectStatus.CANCELLED)) {
            throw new StageException(ErrorMessage.PROJECT_CANCELED);
        }
        if (currentStatus.equals(ProjectStatus.COMPLETED)) {
            throw new StageException(ErrorMessage.PROJECT_COMPLETED);
        }
    }

    public List<StageDto> getFilteredStages(StageFilterDto filterDto) {
        Stream<Stage> stages = stageRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(stages,
                        (stages1, filter) -> filter.apply(stages1, filterDto),
                        Stream::concat)
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(Long deletedStageId, FateOfTasksAfterDelete tasksAfterDelete, Long receivingStageId) {
        switch (tasksAfterDelete) {
            case CLOTHING -> closeTasksAfterDeleteStage(deletedStageId);

            case CASCADE_DELETE -> deleteTasksAfterDeleteStage(deletedStageId);

            case TRANSFER_TO_ANOTHER_STAGE -> transferTasksAfterDeleteStage(deletedStageId, receivingStageId);
        }
    }
    private void closeTasksAfterDeleteStage(Long id) {
        Stage deletedStage = stageRepository.getById(id);
        deletedStage.getTasks()
                .forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        stageRepository.delete(deletedStage);
    }

    private void deleteTasksAfterDeleteStage(Long id) {
        Stage deletedStage = stageRepository.getById(id);
        List<Task> deletedTasks = deletedStage.getTasks();
        taskRepository.deleteAll(deletedTasks);
        stageRepository.delete(deletedStage);
    }

    private void transferTasksAfterDeleteStage(Long deletedStageId, Long receivingStageId) {
        if(receivingStageId == null){
            throw new StageException(ErrorMessage.NULL_ID);
        }
        Stage deletedStage = stageRepository.getById(deletedStageId);
        List<Task> transferredTasks = deletedStage.getTasks();
        stageRepository.getById(receivingStageId).setTasks(transferredTasks);
        stageRepository.delete(deletedStage);
    }

    public StageDto updateStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage.getStageRoles().forEach(
                stageRoles -> getExecutorsForRole(stage, stageRoles));
        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toDto(updatedStage);
    }

    //Возникает ошибка при тестировании данного метода
    private void getExecutorsForRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = getTeamMembersWithTheRole(stage, stageRoles);

        //Разница между необходимым количеством участников для данной роли
        //и тем количеством участников, которые уже работают на этом этапе проекта.
        //Если она больше 0, ищу среди всего проекта (а не только одного этапа) участников с такой же ролью,
        //чтобы затем отправить им приглашение участвовать в данном этапе проекта
        int requiredNumberOfInvitation = stageRoles.getCount() - executorsWithTheRole.size();

        List<TeamMember> projectMembersWithTheSameRole = new ArrayList<>();
        if (requiredNumberOfInvitation > 0) {
            getProjectMembersWithTheSameRole(stage, stageRoles, projectMembersWithTheSameRole);
        }

        if (projectMembersWithTheSameRole.size() < requiredNumberOfInvitation) {  //Если число учасников проекта с нужной ролью
            projectMembersWithTheSameRole.forEach(teamMember -> {                 //меньше, чем количество учасников,
                StageInvitation stageInvitationToSend =                           //которое нужно пригласить,
                        getStageInvitation(teamMember, stage, stageRoles);        //отправляю приглашения тому количеству учасников,
                stageInvitationRepository.save(stageInvitationToSend);            //которые есть в списке
            });

            int numberOfMissingTeamMembers = requiredNumberOfInvitation - projectMembersWithTheSameRole.size();

            //И выводится сообщение о том, сколько еще участников с нужной ролью нужно найти для работы на данном этапе проекта
            throw new StageException("To work at the project stage, " + stageRoles.getCount() +
                    " executor(s) with a role " + stageRoles.getTeamRole().name() + " are required. But there is(are) only " +
                    projectMembersWithTheSameRole.size() + " executor(s) on the project with this role. " +
                    " There is a need for " + numberOfMissingTeamMembers + " more executor(s).");
        }

        projectMembersWithTheSameRole.stream()              //Если же число учасников в списке больше либо равно искомому
                .limit(requiredNumberOfInvitation)          //числу учасников с нужной ролью, отправляю столько приглашений,
                .forEach(teamMember -> {                    //сколько требуется
                    StageInvitation stageInvitationToSend =
                            getStageInvitation(teamMember, stage, stageRoles);
                    stageInvitationRepository.save(stageInvitationToSend);
                });
    }

    private List<TeamMember> getTeamMembersWithTheRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = new ArrayList<>();
        stage.getExecutors().forEach(executor -> {                //Определяю роль каждого участника
            if (executor.getRoles()                               //данного этапа проекта.
                    .contains(stageRoles.getTeamRole())) {        //Если она совпадает с ролью, к которой применяется этот метод,
                executorsWithTheRole.add(executor);               //добавляю ее с список.
            }                                                     //Получаю список участников этапа с данной ролью
        });
        return executorsWithTheRole;
    }

    private void getProjectMembersWithTheSameRole(Stage stage,
                                                         StageRoles stageRoles,
                                                         List<TeamMember> projectMembersWithTheSameRole) {
        stage.getProject()
                .getTeams()                                           //Получаю все команды проекта, к которому относится этап.
                .forEach(team ->                                      //Из каждой команды получаю участников
                        projectMembersWithTheSameRole.addAll(
                                team.getTeamMembers()
                                        .stream()
                                        .filter(teamMember ->                             //Проверяю, не работает ли данный участник
                                                !teamMember.getStages().contains(stage))  //уже над данным этапом
                                        .filter(teamMember ->
                                                teamMember.getRoles()                          //Отбираю участников
                                                        .contains(stageRoles.getTeamRole()))   //с нужной ролью
                                        .toList()));                                           //и добавляю их в отдельный список
    }

    private StageInvitation getStageInvitation(TeamMember invited, Stage stage, StageRoles stageRoles) {
        StageInvitation stageInvitationToSend = new StageInvitation();
        String INVITATIONS_MESSAGE = String.format("Invite you to participate in the development stage %s " +
                "of the project %s for the role %s",
                stage.getStageName(), stage.getProject().getName(), stageRoles.getTeamRole());
        stageInvitationToSend.setDescription(INVITATIONS_MESSAGE);
        stageInvitationToSend.setStatus(StageInvitationStatus.PENDING);
        stageInvitationToSend.setInvited(invited);
        stageInvitationToSend.setStage(stage);
        return stageInvitationToSend;
    }

    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stageMapper.toDtoList(stages);
    }

    public StageDto getStage(Long id) {
        Stage stage = stageRepository.getById(id);

        return stageMapper.toDto(stage);
    }
}
