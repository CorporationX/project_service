package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {
    ResourceDto uploadEntityFile(MultipartFile file, long id);

    InputStream downloadFile(String fileKey);

    void deleteFile(String fileFey);
}
