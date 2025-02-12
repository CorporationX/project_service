package faang.school.projectservice.dto.resource;

import lombok.Data;
import org.springframework.core.io.InputStreamResource;

@Data
public class S3FileDto {
    private InputStreamResource inputStreamResource;
    private String fileName;
    private String contentType;
    private long contentLength;
}
