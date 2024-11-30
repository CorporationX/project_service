package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EmptyResourceException;
import faang.school.projectservice.exception.InsufficientStorageException;
import faang.school.projectservice.exception.InvalidAccessException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceValidator {

    private final ResourceRepository resourceRepository;

    public void validateResourceNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyResourceException(String.format("File %s is empty. It cannot be uploaded", file.getName()));
        }
    }

    public void validateEnoughSpaceInStorage(Project project, MultipartFile file) {
        BigInteger availableStorageSize = project.getMaxStorageSize().subtract(project.getStorageSize());
        BigInteger neededStorageSize = BigInteger.valueOf(file.getSize());

        if (availableStorageSize.compareTo(neededStorageSize) < 0) {
            throw new InsufficientStorageException("Not enough space to store files");
        }
    }

    public void validateResourceExistsById(Long resourceId) {
        if(!resourceRepository.existsById(resourceId)) {
            throw new EntityNotFoundException(String.format("Resource not found, id: %d", resourceId));
        }
    }

    public void validateTeamMemberHasPermissionsToModifyResource(TeamMember teamMember, Resource resource) {
        Long userId = teamMember.getUserId();
        Long createdById = resource.getCreatedBy().getId();
        List<TeamRole> userRoles = teamMember.getRoles();
        if(!(userId.equals(createdById) || userRoles.contains(TeamRole.MANAGER))) {
            throw new InvalidAccessException(String.format("User id: %d doesn't have rights to modify file id: %d",
                    userId, resource.getId()));
        }
    }
}
