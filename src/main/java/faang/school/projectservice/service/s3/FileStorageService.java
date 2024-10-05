package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.image.FileData;

import java.io.InputStream;

public interface FileStorageService {
    FileData getFileByKey(String key);
    void uploadFile(Long id, String fileName, String contentType, int fileSize, InputStream stream);
    void removeFileById(Long id);
}
