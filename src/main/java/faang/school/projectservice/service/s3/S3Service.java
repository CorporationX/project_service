package faang.school.projectservice.service.s3;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile file, String folderName);

    InputStreamResource getFile(String key);

    void deleteFile(String key);
}
