package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceValidator {

    private static final int POWER_OF_THREE = 3;
    private static final int GIGABYTE_MULTIPLIER_TWO = 2;
    private static final int THOUSAND_BYTES = 1000;
    private static final BigInteger STORAGE_SIZE = new BigInteger(String.
            valueOf(Math.round(Math.pow(THOUSAND_BYTES, POWER_OF_THREE) * GIGABYTE_MULTIPLIER_TWO)));

    public void validateTeamMemberBelongsToProject(TeamMember fileOwner, long projectId) {
        long fileOwnersProjectId = fileOwner.getTeam().getProject().getId();
        if (!Objects.equals(fileOwnersProjectId, projectId)) {
            log.error("TeamMember with id {} , doesn't belongs to project {}!", fileOwner.getId(), projectId);
            throw new DataValidationException("You cannot upload files because you don't belongs to the project!");
        }
    }

    public void setNewProjectStorageSize(Project project) {
        if (project.getStorageSize() == null) {
            project.setStorageSize(STORAGE_SIZE);
            log.debug("Set project {} storage for {} GB", project.getName(),
                    byteToGigabyteConverter(STORAGE_SIZE.longValue()));
        }
    }

    public void validateStorageCapacity(MultipartFile file, Project project) {
        if ((BigInteger.valueOf(file.getSize())).compareTo(project.getStorageSize()) > 0) {
            log.error("Maximum capacity reached!");
            log.debug("Current capacity {} GB and file size {} GB",
                    byteToGigabyteConverter(project.getStorageSize().longValue()),
                    byteToGigabyteConverter(file.getSize()));
            throw new DataValidationException("File size is too big or project's storage capacity is overloaded!");
        }
    }

    public long byteToGigabyteConverter(long size) {
        return Math.round(size / Math.pow(THOUSAND_BYTES, POWER_OF_THREE));
    }
}
