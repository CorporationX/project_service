package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.client.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

    ResourceDto addResource(ResourceDto resourceDto);
    ResourceDto addCoveringImageToProject(MultipartFile file, Long projectId);

}
