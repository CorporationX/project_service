package faang.school.projectservice.service.minio;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {
    Resource uploadFile(MultipartFile file, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
