package faang.school.projectservice.service.resource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import faang.school.projectservice.dto.sharedfiles.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.activation.MimetypesFileTypeMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3;
    private final BigInteger MAX_SIZE = BigInteger.valueOf(2000000L);
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ResourceRepository resourceRepository;
    private final String DEFAULT_BUCKET = "project";
    public ResourceDto add(MultipartFile file, TeamMember teamMember, Long projectId) {
        teamMember.setId(4L);

        Project project = projectMapper.toEntity(projectService.getProjectById(projectId));
        LocalDateTime now = LocalDateTime.now();
        String key = file.getName() + now;
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(teamMember.getRoles());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(teamMember);
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);
        resource.setProject(project);
        String path = projectId + "/" + key;

        if (!s3.client.doesBucketExistV2(DEFAULT_BUCKET)) {
            s3.client.createBucket(DEFAULT_BUCKET);
        }

        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        s3.client.putObject(DEFAULT_BUCKET, path, inputStream, data);
        var a = resourceRepository.save(resource);
        return new ResourceDto();
    }

    public ResponseEntity<InputStreamResource> get(Long resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        Long projectId = resource.getProject().getId();

        String path = projectId + "/" + resource.getKey();
        S3Object s3Object = s3.client.getObject(DEFAULT_BUCKET, path);
        InputStream inputStream = s3Object.getObjectContent();

        String mimeType = s3Object.getObjectMetadata().getContentType();
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }
        MediaType mediaType = MediaType.parseMediaType(mimeType);
        String encodedFileName = UriUtils.encode(resource.getName(), StandardCharsets.UTF_8);
        String contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        headers.setContentType(mediaType);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .contentType(mediaType)
                .body(new InputStreamResource(inputStream));

    }
}
