package faang.school.projectservice.dto.resource;

import lombok.Builder;

import java.io.InputStream;

@Builder
public record S3ObjectDto(
        String fileName,
        InputStream inputStream,
        String contentType
) {
}
