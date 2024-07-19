package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final Validator validator;


    // Создание этапа.
    public StageDto createStage(StageDto stageDto) {
        if (validator.validateInputStageData(stageDto)) {
            throw new DataValidationException("Недостаточно данных для создания этапа!");
        }
        if (!stageDto.getProject().getStatus().equals(ProjectStatus.IN_PROGRESS)) {
            throw new DataValidationException("Проект закрыт или отменён!");
        }
        return stageMapper.stageToDto(stageRepository
                .save(stageMapper.stageDtoToEntity(stageDto)));
    }

    // Получить все этапы проекта с фильтром по статусу (в работе, выполнено и т.д).
    public List<StageDto> getAllStagesFilteredByProjectStatus(Long projectId,
                                                              ProjectFilterDto filter) {
        Project projectOptional = Optional.ofNullable(projectRepository.getProjectById(projectId))
                .orElseThrow(() ->
                        new DataValidationException("Такого проекта не существует!"));
        if (validateProjectStatus(projectOptional, filter)) {
            return projectOptional.getStages()
                    .stream()
                    .map(stageMapper::stageToDto)
                    .toList();
        } else {
            throw new DataValidationException("У данного проекта другой статус!");
        }
    }

    // Удалить этап.
    public void deleteStage(Long id) {
        stageRepository.delete(stageRepository.getById(id));
    }

    // Обновить этап.
    public StageDto updateStage(StageDto stageDto, TeamRole teamRole) {
        List<TeamMember> teamMembersToAdd;

        if (!stageRepository.isExistById(stageDto.getStageId())) {
            throw new DataValidationException("Такого этапа не существует!");
        }

        Stage currentStage = stageRepository.getById(stageDto.getStageId());
        List<StageRoles> stageRoles = currentStage.getStageRoles();

        if (checkUsersWithCertainRole(teamRole, stageDto)) {
            // определяем требуемое количество участников с такой ролью
            StageRoles resultStageRoles = stageRoles.stream()
                    .filter(roles -> roles.getTeamRole().equals(teamRole))
                    .toList().get(0);
            int amount = resultStageRoles.getCount();

            teamMembersToAdd = sendInvitations(stageDto.getProject(), teamRole, amount);
        } else {
            throw new DataValidationException("Участник с такой ролью на этапе уже есть!");
        }

        currentStage.getExecutors().addAll(teamMembersToAdd);
        return stageMapper.stageToDto(currentStage);
    }

    // Получить все этапы проекта.
    public List<StageDto> getStagesOfProject(long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        return stageRepository.findAll()
                .stream()
                .filter(stage -> stage.getProject()
                        .equals(project))
                .map(stageMapper::stageToDto)
                .toList();
    }

    // Получить конкретный этап по Id.
    public StageDto getStageById(long stageId) {
        return stageMapper.stageToDto(stageRepository.getById(stageId));
    }

    public boolean validateProjectStatus(Project project, ProjectFilterDto filter) {
        return project.getStatus().toString().equals(filter.getProjectStatus());
    }

    public boolean checkUsersWithCertainRole(TeamRole teamRole, StageDto stageDto) {
        // проверяем, есть ли среди экзекуторов этапа необходимые на этапе роли
        List<TeamRole> resultList = stageDto.getExecutorIds().stream()
                .map(teamMemberRepository::findById)
                .toList()
                .stream()
                .map(TeamMember::getRoles)
                .toList()
                .stream()
                .flatMap(Collection::stream)
                .filter(teamRoleForCompare -> teamRoleForCompare.equals(teamRole))
                .toList();
        return resultList.isEmpty();
    }

    private List<TeamMember> sendInvitations(Project project, TeamRole teamRole, int amount) {
        // составляем список участников проекта
        List<TeamMember> projectTeam = project.getTeams()
                .stream()
                .map(Team::getTeamMembers)
                .flatMap(Collection::stream)
                .toList();
        // ищем пользователей с требуемыми ролями
        List<TeamMember> membersForInviting = projectTeam
                .stream()
                .filter(teamMember -> teamMember.getRoles().contains(teamRole))
                .toList();
        // создаём приглашение для каждого пользователя
        List<TeamMember> resultListToInvite = new ArrayList<>();
        for (TeamMember member : membersForInviting) {
            if (amount > 0) {
                StageInvitation invitation = new StageInvitation();
                invitation.setInvited(member);
                invitation.setStatus(StageInvitationStatus.PENDING);
                resultListToInvite.add(member);
                amount--;
            }
        }
        return resultListToInvite;
    }
}
