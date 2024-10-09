package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.project.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {
    Project getProject(long projectId);

    ProjectDto uploadCover(long projectId, MultipartFile file, long userId);
}
