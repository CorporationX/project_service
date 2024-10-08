package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.config.properties.S3ConfigurationProperties;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.util.converter.MultiPartFileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;
    @Mock
    private AmazonS3Client amazonS3Client;
    @Mock
    private S3ConfigurationProperties config;
    private static final long TEAM_MEMBER_ID_ONE = 1L;
    private static final String FILE_NAME = "BoberNotKurwa";
    private static final String CONTENT_TYPE = "png/image";
    private static final long FILE_TEST_SIZE = 100_000L;
    private static final byte[] INPUT = new byte[(int) FILE_TEST_SIZE];
    private static final long PROJECT_ID_ONE = 1L;
    private static final BigInteger STORAGE_SIZE = new BigInteger(String.valueOf(Math.round(Math.pow(1000, 3))));
    private Resource resource;
    private TeamMember teamMember;
    private MultipartFile file;
    private Project project;
    private ObjectMetadata objectMetadata;


    @BeforeEach
    void setUp() throws IOException {
        List<Resource> resources = new ArrayList<>();
        resources.add(Resource.builder().build());

        project = Project.builder()
                .id(PROJECT_ID_ONE)
                .storageSize(STORAGE_SIZE)
                .resources(resources)
                .ownerId(TEAM_MEMBER_ID_ONE)
                .build();

        teamMember = TeamMember.builder()
                .id(TEAM_MEMBER_ID_ONE)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        file = MultiPartFileConverter.builder()
                .originalFileName(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .input(INPUT)
                .build();

        resource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(file.getOriginalFilename() + "@" + BigInteger.valueOf(file.getSize()))
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();

        objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getResource().contentLength());
        objectMetadata.setContentType(file.getContentType());
    }

    @Nested
    class classMethodTests {
        @Test
        @DisplayName("When MultiPartFile and resource passed, save it in s3")
        public void whenMultipartFileAndResourcePassedThenSaveItInS3Storage() {
            s3Service.saveObject(file, resource);
        }

        @Test
        @DisplayName("When valid Resource passed then delete it from s3")
        public void whenResourcePassedThenDeleteItFromS3storage() {
            s3Service.deleteObject(resource.getKey(), resource.getProject().getName());
        }

        @Test
        @DisplayName("When valid Resource passed then start downloading")
        public void whenResourcePassedThenGetFileFromS3Storage() {
            s3Service.getObject(resource);
        }
    }

    @Nested
    class ExceptionsTests {
        @Test
        @DisplayName("When SdkClientException occurs throws RuntimeException")
        public void whenIOExceptionOccursThenThrowException() throws Exception {
            MultipartFile invalidFile = mock(MultipartFile.class);
            when(invalidFile.getResource()).thenReturn((mock(org.springframework.core.io.Resource.class)));
            when(invalidFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

            doThrow(new SdkClientException("Test sdkClientExceptionMessage")).when(amazonS3Client)
                    .putObject(any(PutObjectRequest.class));

            assertThrows(RuntimeException.class, () ->
                    s3Service.saveObject(invalidFile, resource));
        }

        @Test
        @DisplayName("When amazonServiceException occurs throws RuntimeException")
        public void whenAmazonServiceExceptionOccursThenThrowRuntimeException() throws Exception {
            MultipartFile invalidFile = mock(MultipartFile.class);

            when(invalidFile.getResource()).thenReturn(mock(org.springframework.core.io.Resource.class));
            when(invalidFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

            doThrow(new AmazonServiceException("Test amazonServiceExceptionMessage")).when(amazonS3Client)
                    .putObject(any(PutObjectRequest.class));

            assertThrows(RuntimeException.class, () ->
                    s3Service.saveObject(invalidFile, resource));
        }

        @Test
        @DisplayName("When IOException occurs while uploading throws RuntimeException")
        public void whenIOExceptionOccursThenThrowRuntimeException() throws IOException {
            MultipartFile invalidFile = mock(MultipartFile.class);
            when(invalidFile.getResource()).thenReturn(mock(org.springframework.core.io.Resource.class));
            when(invalidFile.getInputStream()).thenThrow(new IOException("Uploading canceled due to error"));

            assertThrows(RuntimeException.class, () ->
                    s3Service.saveObject(invalidFile, resource));
        }

        @Test
        @DisplayName("When SDK exception occurs while deleting file throws RuntimeException")
        public void whenSDKExceptionOccursWhileDeletingFileThenThrowRuntimeException() {
            doThrow(new SdkClientException("Test sdk exception message")).when(amazonS3Client)
                    .deleteObject(any(DeleteObjectRequest.class));

            assertThrows(RuntimeException.class, () ->
                    s3Service.deleteObject(resource.getKey(),resource.getProject().getName()));
        }

        @Test
        @DisplayName("When ASE exception occurs while deleting file throws RuntimeException")
        public void whenASEExceptionOccursWhileDeletingFileThenThrowRuntimeException() {
            doThrow(new AmazonServiceException("Test ASE exception message")).when(amazonS3Client)
                    .deleteObject(any(DeleteObjectRequest.class));

            assertThrows(RuntimeException.class, () ->
                    s3Service.deleteObject(resource.getKey(),resource.getProject().getName()));
        }

        @Test
        @DisplayName("When SDK exception occurs while getting file throws RuntimeException")
        public void whenSDKExceptionOccursWhileGettingFileThenThrowRuntimeException() {
            doThrow(new SdkClientException("Test sdk exception message")).when(amazonS3Client)
                    .getObject(any(GetObjectRequest.class));

            assertThrows(RuntimeException.class, () ->
                    s3Service.getObject(resource));
        }

        @Test
        @DisplayName("When SDK exception occurs while getting file throws RuntimeException")
        public void whenASEExceptionOccursWhileGettingFileThenThrowRuntimeException() {
            doThrow(new AmazonServiceException("Test ASE exception message")).when(amazonS3Client)
                    .getObject(any(GetObjectRequest.class));

            assertThrows(RuntimeException.class, () ->
                    s3Service.getObject(resource));
        }
    }
}
