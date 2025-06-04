package faang.school.projectservice.validation;

import faang.school.projectservice.exception.StorageException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;

import java.math.BigInteger;
import java.util.Objects;

public class StorageValidation {

    public static void storageSizeNotExceededValidation(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new StorageException("Storage size limit exceeded");
        }
    }

    public static void storageFileAccessPermissionCheck(Resource resource, TeamMember teamMember) {
        if (resource.getAllowedRoles().stream().noneMatch(teamMember.getRoles()::contains)) {
            throw new StorageException("You do not have permission to delete this file");
        }
    }

    public static void projectResourcesAccessPermissionCheck(Project project, TeamMember teamMember) {
        if (!Objects.equals(project.getId(), teamMember.getTeam().getProject().getId())) {
            throw new StorageException("You do not have permission to download file from this project");
        }
    }
}
