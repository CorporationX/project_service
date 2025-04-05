package faang.school.projectservice.service.projectresource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.ResourceHandlingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    void uploadFile(MultipartFile file, String key) throws IOException;

    void deleteFile(String key);

    S3Object downloadFile(String key) throws ResourceHandlingException;
}

