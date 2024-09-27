package faang.school.projectservice.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile file, String folderName);
}
