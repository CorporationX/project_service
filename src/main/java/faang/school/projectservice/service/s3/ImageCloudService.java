package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.image.FileData;

import java.io.InputStream;

public interface ImageCloudService {
    FileData getObjectByKey(String key);
    String uploadObject(String fileName, String contentType, int fileSize, InputStream stream);
    void removeObjectByKey(String key);
}
