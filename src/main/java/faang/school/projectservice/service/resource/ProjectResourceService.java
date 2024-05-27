package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectResourceService {

    ResourceDto saveFile(long userId, long projectId, MultipartFile file);

    InputStreamResource getFile(long userId, long projectId, long resourceId);

    void deleteFile(long userId, long projectId, long resourceId);
}
