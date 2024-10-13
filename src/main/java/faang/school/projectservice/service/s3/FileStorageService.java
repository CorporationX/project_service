package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface FileStorageService {
    S3Object getObject(Resource resource);
    void saveObject(MultipartFile file, Resource resource);
    void deleteObject(String key, String projectName);
}
