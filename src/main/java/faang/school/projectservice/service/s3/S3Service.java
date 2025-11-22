package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    ResourceDto uploadImage(MultipartFile file, String folder) throws IOException;
    void deleteFile(String key);
    InputStream downloadFile(String key);

}
