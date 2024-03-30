package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.FilerException;
import java.io.IOException;
import java.util.logging.ErrorManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Project uploadFile(MultipartFile file, String folder){
        long fileSize = file.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(file.getContentType());
        String key = String.format("%s/%d%s",folder,file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key , file.getInputStream(), objectMetaData);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error((e.getMessage()));
            throw new FilerException(ErrorMessage.FILE_EXCEPTION);
        }

    }
}
