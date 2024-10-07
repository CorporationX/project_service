package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.response.ResourceResponseObject;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    void uploadFile(MultipartFile file, String key);

    void uploadFile(byte[] fileContent, String contentType, String key);

    void deleteFile(String key);

    ResourceResponseObject downloadFile(String key);
}
