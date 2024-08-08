package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.math.BigInteger;
import java.util.List;

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
        List<TeamMember> teemManager = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()
                        .filter(tm -> tm.getRoles().contains(TeamRole.MANAGER)))
                .toList();
        long count = teemManager.stream()
                .filter(tm -> tm.getUserId() == userId)
                .count();
        boolean equals = resource.getCreatedBy().getUserId().equals(userId);
        if (count == 0 && !equals) {
            log.info("the user is not the author of the photo or the project manager");
            throw new RuntimeException("you can't delete this file");
        }
    }
}
