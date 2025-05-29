package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface CloudService {
    Resource uploadFile(MultipartFile file, String folder);

    void deleteFile(String fileKey);

    InputStream downloadFile(String fileKey);
}