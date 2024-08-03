package faang.school.projectservice.service.s3;

import faang.school.projectservice.model.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface S3Service {

    Project uploadFile(MultipartFile file, String folder) throws IOException;

    void deleteFile(String key);

    ByteArrayOutputStream downloadFile(String key);
}
