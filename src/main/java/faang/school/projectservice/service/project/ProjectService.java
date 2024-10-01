package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {
    Project getProject(long projectId);

    ProjectDto uploadCover(long projectId, MultipartFile file, long userId);
}
