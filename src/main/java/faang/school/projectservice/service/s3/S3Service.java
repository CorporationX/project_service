package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.model.Resource;

public interface S3Service {

    Resource uploadFile(byte[] bytes, String folderName, String originalFileName, ObjectMetadata metadata, Long projectId);

    void deleteFile(String key);

}
