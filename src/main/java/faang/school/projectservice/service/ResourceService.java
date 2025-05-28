package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface ResourceService {
    Resource uploadEntityFile(MultipartFile file, long id);

    URL getFileUrl(String fileKey);

    void deleteFile(String fileFey);
}
