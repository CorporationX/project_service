package faang.school.projectservice.service.s3;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder);
    void deleteFile(String key);
    InputStream downloadFile(String key);
}
