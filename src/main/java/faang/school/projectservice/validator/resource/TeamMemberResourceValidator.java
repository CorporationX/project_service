package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.NoAccessException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class TeamMemberResourceValidator {
    public void validateDownloadFilePermission(TeamMember teamMember, Resource resource) {
        if (!resource.getProject().equals(teamMember.getTeam().getProject())) {
            throw new NoAccessException("Only members of this project can download files from storage.");
        }
    }

    public void validateDeleteFilePermission(TeamMember teamMember, Resource resource) {
        if (!resource.getCreatedBy().equals(teamMember)
                && (!teamMember.getRoles().contains(TeamRole.MANAGER) || !teamMember.getTeam().getProject().equals(resource.getProject()))
                && (!teamMember.getRoles().contains(TeamRole.OWNER) || !teamMember.getTeam().getProject().equals(resource.getProject()))) {
            throw new NoAccessException("Только создатель файла или менеджер проекта этого проекта может удалить файл.");
        }
    }
}
