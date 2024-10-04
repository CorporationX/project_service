package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.image.FileData;

import java.io.InputStream;

public interface FileStorageService {
    FileData getFileByKey(String key);
    String uploadFile(String fileName, String contentType, int fileSize, InputStream stream);
    void removeFileByKey(String key);
}
