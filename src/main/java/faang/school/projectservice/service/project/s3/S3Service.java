package faang.school.projectservice.service.project.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadCoverImage(MultipartFile file, String folder);
}
