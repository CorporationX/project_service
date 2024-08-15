package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.math.BigInteger;

@Component
@Slf4j
public class ResourceValidator {
    public void checkStorageSizeExceeded(BigInteger newStorageSize, BigInteger projectMaxStorageSize) {
        if (newStorageSize.compareTo(projectMaxStorageSize) >= 0) {
            log.info("{} is less than {}", projectMaxStorageSize.toString(), newStorageSize);
            throw new MaxUploadSizeExceededException(projectMaxStorageSize.longValue());
        }
    }

    public void checkingAccessRights(long userId, Resource resource, Project project) {
        boolean isManager = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(tm -> tm.getRoles().contains(TeamRole.MANAGER) && tm.getUserId() == userId);
        boolean isAuthor = resource.getCreatedBy().getUserId() == userId;

        if (!isManager && !isAuthor) {
            log.info("the user is not the author of the photo or the project manager");
            throw new DataValidationException("you can't delete this file");
        }
    }
}

