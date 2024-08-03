package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.StorageSizeException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.stream.Stream;

@Component
public class ResourceValidator {
        public void validateStorageOverflow(Project project, MultipartFile file){
            if (project.getMaxStorageSize() == null){
                throw new RuntimeException("Project max storage size is null");
            }
            if (project.getStorageSize() == null){
                throw new RuntimeException("Project storage size is null");
            }
            BigInteger resultSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));


            if (resultSize.compareTo(project.getMaxStorageSize()) > 0) {
                double neededSizeMb = resultSize.subtract(project.getMaxStorageSize()).doubleValue() / 1024 / 1024;
                DecimalFormat df = new DecimalFormat("#.##");
                throw new StorageSizeException("Storage is full! Needed " + df.format(neededSizeMb) + " MB more" );
            }
        }

        public TeamMember getTeamMember(Project project, long userId){
            return project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(member -> member.getUserId().equals(userId))
                    .findFirst().orElseThrow(() -> new AccessDeniedException("User "+userId+" are not a team member of project "+project.getId()));
        }

        public TeamMember validateAreUserATeamMember(Project project, long userId){
            return getStreamTeamMembers(project,userId)
                    .findFirst().orElseThrow(() -> new AccessDeniedException("User "+userId+" are not a team member of project "+project.getId()));
        }

        private Stream<TeamMember> getStreamTeamMembers(Project project, long userId){
            return project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(member -> member.getUserId().equals(userId));
        }
}
