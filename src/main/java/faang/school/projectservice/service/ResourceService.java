package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.dto.ResourceDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    S3Object getResource(Long resourceId);

    @Transactional
    ResourceDto updateResource(Long resourceId, MultipartFile file);

    @Transactional
    void deleteResource(Long resourceId);

    @Transactional
    ResourceDto addResource(Long projectId, MultipartFile file);
}
