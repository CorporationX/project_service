package faang.school.projectservice.service.resource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import faang.school.projectservice.dto.sharedfiles.ResourceDto;
import faang.school.projectservice.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.time.LocalDateTime;



@Service
public class ResourceService {
    private final BigInteger MAX_SIZE = BigInteger.valueOf(2000000L);

    AWSCredentials credentials = new BasicAWSCredentials(
            "<AWS accesskey>",
            "<AWS secretkey>"
    );
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    public ResourceDto uploadFile(MultipartFile file, TeamMember teamMember, Project project) throws IOException {
        Resource resource = new Resource();
        resource.setKey(file.getName() + LocalDateTime.now());
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(teamMember.getRoles());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(teamMember);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setProject(project);

        String bucketName = "";
        String key = file.getName() + LocalDateTime.now();
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());
        s3.putObject(bucketName, key, file.getInputStream(), data);

        return new ResourceDto();
    }
}
