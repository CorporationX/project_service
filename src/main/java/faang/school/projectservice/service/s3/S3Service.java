package faang.school.projectservice.service.s3;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder);

    List<String> uploadFiles(List<MultipartFile> files, Long folder);

    InputStream downloadFile(String key);

    void deleteFile(String key);

    void validateAndCheckFileSizes(List<MultipartFile> files, long maxSize);
}
