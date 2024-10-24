package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.dto.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    S3Object getResource(Long resourceId);

    ResourceDto updateResource(Long resourceId, MultipartFile file);

    void deleteResource(Long resourceId);

    ResourceDto addResource(Long projectId, MultipartFile file);
}
