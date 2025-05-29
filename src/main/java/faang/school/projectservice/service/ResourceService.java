package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {
    InputStream downloadResource(long resourceId);

    Resource uploadResource(MultipartFile file, long projectId);

    Resource deleteResource(long resourceId);
}