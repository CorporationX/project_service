package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {
    ResourceDto uploadFile(long id, MultipartFile file);

    InputStream downloadFile(String key);

    void deleteFile(String key);
}
