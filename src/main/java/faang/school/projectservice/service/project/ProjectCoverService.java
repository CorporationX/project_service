package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCoverDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectCoverService {

    ProjectCoverDto save(long projectId,  MultipartFile file);

    InputStreamResource getByProjectId(long projectId);

    ProjectCoverDto changeProjectCover(long projectId,  MultipartFile file);


    ProjectCoverDto deleteByProjectId(long projectId);
}
