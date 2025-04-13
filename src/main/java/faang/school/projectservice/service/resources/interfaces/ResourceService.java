package faang.school.projectservice.service.resources.interfaces;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

    @Transactional
    ResourceDto addResource(Long projectId, MultipartFile file);

    @Transactional
    void deleteResource(Long projectId, Long resourceId);

    @Transactional(readOnly = true)
    List<ResourceDto> getResources(Long projectId);

    @Transactional
    ResourceDto updateResource(Long resourceId, Long projectId, MultipartFile file);
}
