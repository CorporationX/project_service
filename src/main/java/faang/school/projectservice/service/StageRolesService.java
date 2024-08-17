package faang.school.projectservice.service;

import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageRolesService {

    private final StageRolesRepository stageRolesRepository;
    private final StageInvitationService stageInvitationService;
    private final TeamMemberService teamMembersService;

    public void saveAll(List<StageRoles> stageRolesList) {
        stageRolesRepository.saveAll(stageRolesList);
    }

    public void getExecutorsForRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = teamMembersService.getTeamMembersWithTheRole(stage, stageRoles);

        //Разница между необходимым количеством участников для данной роли
        //и тем количеством участников, которые уже работают на этом этапе проекта.
        //Если она больше 0, ищу среди всего проекта (а не только одного этапа) участников с такой же ролью,
        //чтобы затем отправить им приглашение участвовать в данном этапе проекта
        int requiredNumberOfInvitation = stageRoles.getCount() - executorsWithTheRole.size();

        List<TeamMember> projectMembersWithTheSameRole = new ArrayList<>();
        if (requiredNumberOfInvitation > 0) {
            projectMembersWithTheSameRole =
                    teamMembersService.getProjectMembersWithTheSameRole(stage, stageRoles);
        }

        if (projectMembersWithTheSameRole.size() < requiredNumberOfInvitation) {      //Если число учасников проекта с нужной ролью
            projectMembersWithTheSameRole.forEach(teamMember -> {                     //меньше, чем количество учасников,
                StageInvitation stageInvitationToSend =                               //которое нужно пригласить,
                        stageInvitationService                                        //отправляю приглашения тому количеству учасников,
                                .createStageInvitation(teamMember, stage, stageRoles);   //которые есть в списке
            });

            int numberOfMissingTeamMembers = requiredNumberOfInvitation - projectMembersWithTheSameRole.size();

            //И выводится сообщение о том, сколько еще участников с нужной ролью нужно найти для работы на данном этапе проекта
            log.warn("To work at the project stage, a total of {} executor(s) with a role {} are required. " +
                            "At this project stage {} team member(s) are already working. " +
                            "{} more executor(s) should be invited. " +
                            "But there is(are) only {} team member(s) on the project with this role. " +
                            "Invitations have been sent to them. " +
                            "There is a need for {} more executor(s).",
                    stageRoles.getCount(), stageRoles.getTeamRole().name(), executorsWithTheRole.size(),
                    requiredNumberOfInvitation, projectMembersWithTheSameRole.size(), numberOfMissingTeamMembers);
        } else {
            projectMembersWithTheSameRole.stream()              //Если же число учасников в списке больше либо равно искомому
                    .limit(requiredNumberOfInvitation)          //числу учасников с нужной ролью, отправляю столько приглашений,
                    .forEach(teamMember -> {                    //сколько требуется
                        StageInvitation stageInvitationToSend =
                                stageInvitationService.createStageInvitation(teamMember, stage, stageRoles);
                    });
        }
    }
}
