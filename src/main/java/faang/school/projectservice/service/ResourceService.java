package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    ResourceDto addResource(MultipartFile file, Long projectId, Long currentMemberId);

    void deleteResource(Long id, Long currentMemberId);

}
