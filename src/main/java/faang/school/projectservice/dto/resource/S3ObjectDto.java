package faang.school.projectservice.dto.resource;

import com.amazonaws.services.s3.model.S3Object;
import lombok.Builder;

@Builder
public record S3ObjectDto(
        String fileName,
        S3Object s3Object,
        String contentType
) {
}
