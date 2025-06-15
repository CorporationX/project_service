package faang.school.projectservice.service.s3;

import java.io.File;
import java.io.InputStream;

public interface S3Service {
    public String uploadFile(File file, String contentType, String folder);
    public InputStream downloadFile(String objectKey);
    public String getAvatarContentType(String objectKey);
    public void deleteFile(String key);
}
