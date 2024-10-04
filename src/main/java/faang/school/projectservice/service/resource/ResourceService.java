package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {

    ResourceDto addResource(Long projectId, MultipartFile file);

    void deleteResource(Long resourceId);

    InputStream downloadResource(Long resourceId);

    String getResourceName(Long resourceId);

}
