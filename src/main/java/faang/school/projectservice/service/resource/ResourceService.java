package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

    ResourceDto addResource(Long projectId, MultipartFile file);

    void deleteResource(Long resourceId);

    ResourceDownloadDto downloadResource(Long resourceId);

    String getResourceName(Long resourceId);

}
