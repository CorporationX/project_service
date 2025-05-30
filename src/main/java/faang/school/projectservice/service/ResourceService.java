package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface ResourceService {
    ResourceDto uploadEntityFile(MultipartFile file, long id);

    URL getFileUrl(String fileKey);

    void deleteFile(String fileFey);
}
