package faang.school.projectservice.service.coverimage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoverImageService {
    private final ProjectRepository projectRepository;
    private final CoverImageSizeHandler coverImageSizeHandler;
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public MultipartFile create(Long projectId, MultipartFile file) {
        MultipartFile multipartFile = coverImageSizeHandler.validateSizeAndResolution(file);
        Project project = projectRepository.getProjectById(projectId);
        String key = putIntoBucket(multipartFile);
        project.setCoverImageId(key);
        projectRepository.save(project);

        return multipartFile;
    }

    private String putIntoBucket(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = getMetadata(multipartFile);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            String key = System.currentTimeMillis() + multipartFile.getOriginalFilename();
            PutObjectRequest request = new PutObjectRequest(bucketName, key,
                    multipartFile.getInputStream(), objectMetadata);
            amazonS3.putObject(request);

            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed uploading file: " + multipartFile.getName());
        }
    }

    private ObjectMetadata getMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        return objectMetadata;
    }

    public InputStream downloadImage(String key) {
        return amazonS3.getObject(bucketName, key)
                .getObjectContent();
    }
}
