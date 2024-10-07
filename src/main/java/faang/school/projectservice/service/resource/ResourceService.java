package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.project.resource.ResourceDto;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

    ResourceResponseObject getResourceById(Long resourceId);

    void deleteResourceById(Long resourceId);

    ResourceDto updateResourceById(Long resourceId, MultipartFile file);

    ResourceDto addResourceToProject(Long projectId, MultipartFile file);
}
