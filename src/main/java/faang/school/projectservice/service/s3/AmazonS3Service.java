package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface AmazonS3Service {

    String uploadFile(String path, MultipartFile file);

    String uploadFile(String path, Pair<InputStream, ObjectMetadata> file);


    InputStreamResource downloadFile(String key);

    void deleteFile(String key);
}
