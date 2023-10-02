package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.project.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceCreationDto {
    private MultipartFile multipartFile;
    private Project project;
    private TeamMember teamMember;
    private String fileKey;
}
