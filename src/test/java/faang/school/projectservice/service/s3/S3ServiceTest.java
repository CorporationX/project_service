package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"services.s3.bucketName=test-bucket"})
public class S3ServiceTest {

    @InjectMocks
    S3ServiceImpl s3Service;

    @Mock
    AmazonS3 s3Client;

    private String folder = "folder";
    private MultipartFile multipartFile;
    private byte[] fileContent;
    private String resourceKey = "resourceKey";
    private String bucketName;



    @BeforeEach
    void setUp() {
        fileContent = new byte[2048];
        multipartFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                fileContent
        );
    }

    @Test
    void uploadFile_validRequest_returnsResource() {
        Resource result = s3Service.uploadFile(multipartFile, folder);
        verify(s3Client).putObject(any(PutObjectRequest.class));
        assertEquals(result.getName(), multipartFile.getOriginalFilename());
    }

    @Test
    void deleteFile_validRequest_s3ClientCalled() {
        s3Service.deleteFile(resourceKey);
        verify(s3Client).deleteObject(bucketName, resourceKey);
    }
}
