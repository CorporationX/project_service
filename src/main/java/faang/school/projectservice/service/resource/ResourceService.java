package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

    ResourceDto addProjectAvatar(long projectId, MultipartFile file);
}