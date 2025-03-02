package faang.school.projectservice.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {

    void uploadToS3(String keyName, InputStream inputStream, long contentLength, MultipartFile file);
    void deleteFromS3(String keyName);

}
