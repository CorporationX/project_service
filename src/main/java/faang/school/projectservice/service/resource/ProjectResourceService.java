package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ProjectResourceService {

    ResourceDto saveFile(long userId, long projectId, MultipartFile file);

    InputStream getFile(long userId, long projectId, long resourceId);

    void deleteFile(long userId, long projectId, long resourceId);
}
