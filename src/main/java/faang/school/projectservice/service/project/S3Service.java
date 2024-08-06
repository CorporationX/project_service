package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder);
}
