package faang.school.projectservice.service.s3;

import faang.school.projectservice.model.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface S3Service {

    Project uploadImage(MultipartFile file, Project project) throws IOException;

    void deleteImage(String key);

    ByteArrayOutputStream downloadImage(String key);
}
