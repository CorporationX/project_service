package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {
    ResourceResponseDto addResource(Long userId, Long projectId, MultipartFile file);

    InputStream downloadResource(Long userId, Long resourceId);

    void deleteResource(Long userId, Long resourceId);
}
