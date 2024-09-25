package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    ResourceDto saveResource(long projectId, MultipartFile file, long userId);

    InputStreamResource getFile(long projectId, long userId, long resourceId);

    void deleteResource(long projectId, long userId, long resourceId);
}
