package faang.school.projectservice.service.s3;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "true")
public class S3ServiceMock implements S3Service {

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        String key = String.format("%s/%s_%d",
                folder,
                file.getOriginalFilename(),
                ZonedDateTime.now().toInstant().toEpochMilli()
        );

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());

        return resource;
    }

    @Override
    public void deleteFile(String key) {
        log.info("file is deleted");
    }

    @Override
    public InputStream downloadFile(String key) {
        return InputStream.nullInputStream();
    }
}
