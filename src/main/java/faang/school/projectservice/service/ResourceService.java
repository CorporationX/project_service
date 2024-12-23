package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ExtendedResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

    ResourceDto addResource(Long projectId, MultipartFile file);
    String uploadProjectCover(Long projectId, MultipartFile file);
    ExtendedResourceDto downloadResource(Long resourceId);
    void deleteResource(long resourceId, long userId);
    ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file);
}
