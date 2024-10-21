package faang.school.projectservice.service.project.cover;

import org.springframework.web.multipart.MultipartFile;

public interface ProjectCoverService {
    String uploadProjectCover(Long projectId, MultipartFile file);
    void deleteProjectCover(Long projectId);
}