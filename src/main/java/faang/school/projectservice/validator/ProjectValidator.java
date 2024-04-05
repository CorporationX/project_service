package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataAccessException;
import faang.school.projectservice.model.Project;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProjectValidator {

    public void checkUserIsMemberOrThrowException(Project project, long requestUserId) {
        if (!isMember( project, requestUserId )) {
            throw new DataAccessException( "User ID " + requestUserId + " is not a member of private project " + project.getId() );
        }

    }

    public boolean isMember(Project project, long userId) {
        return project.getOwnerId() == userId || project.getTeams().stream()
                .flatMap( team -> team.getTeamMembers().stream() )
                .anyMatch( teamMember -> teamMember.getUserId() == userId );

    }
}
