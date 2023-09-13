package faang.school.projectservice.s3;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    Resource uploadFile(MultipartFile multipartFile, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}