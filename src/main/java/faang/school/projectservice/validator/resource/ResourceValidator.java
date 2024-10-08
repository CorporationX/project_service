package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.util.converter.GigabyteConverter;
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

    private final GigabyteConverter gigabyteConverter;

    public void validateTeamMemberBelongsToProject(TeamMember fileOwner, long projectId) {
        long fileOwnersProjectId = fileOwner.getTeam().getProject().getId();
        if (!Objects.equals(fileOwnersProjectId, projectId)) {
            throw new ForbiddenException("TeamMember with id " + fileOwner.getId()  +
                    " doesn't belong to project " + projectId + "!");
        }
    }

    public void validateStorageCapacity(MultipartFile file, Project project) {
        if ((BigInteger.valueOf(file.getSize())).compareTo(project.getStorageSize()) > 0) {
            log.error("Maximum capacity reached!");
            log.debug("Current capacity {} GB and file size {} GB",
                    gigabyteConverter.byteToGigabyteConverter(project.getStorageSize().longValue()),
                    gigabyteConverter.byteToGigabyteConverter(file.getSize()));
            throw new DataValidationException("File size is too big or project's storage capacity is overloaded!");
        }
    }
}
