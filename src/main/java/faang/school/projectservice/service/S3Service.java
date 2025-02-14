package faang.school.projectservice.service;

import java.io.InputStream;

public interface S3Service {
    String uploadFile(InputStream file, String name, String contentType, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
