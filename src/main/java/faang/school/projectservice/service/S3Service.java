package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String entityName);

    URL getFileUrl(String fileKey);

    void deleteFile(String fileKey);
}
