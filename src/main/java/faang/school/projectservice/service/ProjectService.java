package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ProjectService {
    Project getProjectById(Long id);

    ProjectDto uploadCoverImage(Long projectId, MultipartFile coverImage);

    InputStream downloadCoverImage(Long projectId);

    ProjectDto deleteCoverImage(Long projectId);

    ProjectDto getProject(long projectId);

    void setMaxWidth(int maxWidth);

    void setMaxHeightHorizontal(int maxHeightHorizontal);

    void setMaxHeightSquare(int maxHeightSquare);

    void setMaxFileSize(long maxFileSize);
}
