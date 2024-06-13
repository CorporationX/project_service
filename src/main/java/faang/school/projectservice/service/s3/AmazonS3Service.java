package faang.school.projectservice.service.s3;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface AmazonS3Service {

    String uploadFile(String path, MultipartFile file);

    InputStreamResource downloadFile(String key);

    void deleteFile(String key);
}
