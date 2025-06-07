package faang.school.projectservice.dto.resource;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

@Builder
@Data
public class S3FileDto {
    private Resource resource;
    private String fileName;
    private String contentType;
    private long contentLength;
}
