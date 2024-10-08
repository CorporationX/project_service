package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.image.FileData;

import java.io.InputStream;

public interface FileStorageService {
    FileData getFileById(String id);
    void uploadFile(String id, String fileName, String contentType, int fileSize, InputStream stream);
    void removeFileById(String id);
}
