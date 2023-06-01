package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class StageValidator {
    public void validateStageDto(StageDto stageDto, Project project) {
        List<TeamMember> teamMembers = project.getTeam().getTeamMembers();
        List<StageRolesDto> stageRolesDtos = stageDto.getStageRolesDtos();

        int teamSize = teamMembers.size();
        int stageRolesSize = stageRolesDtos.size();
        if (teamSize < stageRolesSize) {
            throw new DataValidationException(String
                    .format("The size of the planned stage %s cannot be larger then %s", teamSize, stageRolesSize));
        }
        String exceptionString = validateForExcessOfInvited(teamMembers, stageRolesDtos);
        if (exceptionString.length() > 0) {
            throw new DataValidationException(exceptionString);
        }
    }

    private String validateForExcessOfInvited(List<TeamMember> teamMembers, List<StageRolesDto> stageRolesDtos) {
        final Map<TeamRole, Integer> teamsMap = new EnumMap<>(TeamRole.class);
        for (StageRolesDto stageRolesDto : stageRolesDtos) {
            TeamRole teamRole = stageRolesDto.getTeamRole();
            teamsMap.put(teamRole, teamsMap.getOrDefault(teamRole, 0) + stageRolesDto.getCount());
        }
        for (TeamMember teamMember : teamMembers) {
            List<TeamRole> teamRoles = teamMember.getRoles();
            for (TeamRole teamRole : teamRoles) {
                int count = teamsMap.get(teamRole);
                if (count == 0) {
                    break;
                }
                count--;
                teamsMap.put(teamRole, count);
            }
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<TeamRole, Integer> entry : teamsMap.entrySet()) {
            if (entry.getValue() > 0) {
                builder.append(String.format("The team is missing %s more %sS", entry.getValue(), entry.getKey().toString()));
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
