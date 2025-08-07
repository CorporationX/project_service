package faang.school.projectservice.service.s3;

import java.io.InputStream;

public interface S3Service {
    void upload(InputStream inputStream, String key, long contentLength, String contentType);
    InputStream download(String key);
    void delete(String key);
}
