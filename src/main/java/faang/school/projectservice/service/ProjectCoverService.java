package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProjectCoverService {
    String uploadProjectCover(Long projectId, MultipartFile file) throws Exception;

}
