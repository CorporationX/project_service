package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.projectservice.dto.FileDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.properties.AmazonS3Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private AmazonS3Properties amazonS3Properties;

    private final FileDto file = FileDto.builder()
            .originalFilename("originalFilename")
            .key("key")
            .contentType("contentType")
            .inputStream(InputStream.nullInputStream())
            .size(1)
            .build();

    @Test
    void uploadFile_ShouldUpload() {
        Resource resource = s3Service.uploadFile(file);

        assertEquals(file.getKey(), resource.getKey());
        assertEquals(BigInteger.valueOf(file.getSize()), resource.getSize());
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
        assertEquals(ResourceType.getResourceType(file.getContentType()), resource.getType());
        assertEquals(file.getOriginalFilename(), resource.getName());
    }

}