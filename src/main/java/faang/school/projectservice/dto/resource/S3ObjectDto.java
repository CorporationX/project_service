package faang.school.projectservice.dto.resource;

import lombok.Builder;
import org.springframework.core.io.InputStreamResource;

@Builder
public record S3ObjectDto(
        String fileName,
        InputStreamResource inputStream,
        String contentType
) {
}
