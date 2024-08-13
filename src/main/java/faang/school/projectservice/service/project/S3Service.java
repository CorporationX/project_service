package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder, int width, int height);

    InputStream downloadFile(String key);
}
