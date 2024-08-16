package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> findAllById(List<Long> ids) {
        return teamMemberRepository.findAllById(ids);
    }

    public List<TeamMember> getTeamMembersWithTheRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = new ArrayList<>();
        stage.getExecutors().forEach(executor -> {                //Определяю роль каждого участника
            if (executor.getRoles()                               //данного этапа проекта.
                    .contains(stageRoles.getTeamRole())) {        //Если она совпадает с ролью, к которой применяется этот метод,
                executorsWithTheRole.add(executor);               //добавляю ее с список.
            }                                                     //Получаю список участников этапа с данной ролью
        });
        return executorsWithTheRole;
    }

    public List<TeamMember> getProjectMembersWithTheSameRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> projectMembersWithTheSameRole = new ArrayList<>();
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
    return projectMembersWithTheSameRole;
    }
}
